package com.bscan.app.core.data.repository

import android.net.Uri
import com.bscan.app.core.data.db.PageEntity
import com.bscan.app.core.data.db.ProjectDao
import com.bscan.app.core.data.db.ProjectEntity
import com.bscan.app.core.data.db.ProjectWithPages
import com.bscan.app.core.data.storage.AppFileManager
import com.bscan.app.core.model.ScanPage
import com.bscan.app.core.model.ScanProject
import com.bscan.app.core.model.StorageUsageSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface ProjectRepository {
    fun observeAllProjects(): Flow<List<ScanProject>>
    fun observeRecentProjects(limit: Int = 5): Flow<List<ScanProject>>
    fun observeProject(projectId: String): Flow<ScanProject?>
    suspend fun importImagesAsNewProject(uris: List<Uri>): Result<ScanProject>
    suspend fun addImagesToProject(projectId: String, uris: List<Uri>): Result<Int>
    suspend fun renameProject(projectId: String, newName: String)
    suspend fun deleteProject(projectId: String)
    suspend fun getStorageUsage(): StorageUsageSummary
    suspend fun clearPreviewCache()
}

class LocalProjectRepository(
    private val projectDao: ProjectDao,
    private val fileManager: AppFileManager,
) : ProjectRepository {

    override fun observeAllProjects(): Flow<List<ScanProject>> {
        return projectDao.observeAllProjectsWithPages().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRecentProjects(limit: Int): Flow<List<ScanProject>> {
        return projectDao.observeRecentProjectsWithPages(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeProject(projectId: String): Flow<ScanProject?> {
        return projectDao.observeProjectWithPages(projectId).map { it?.toDomain() }
    }

    override suspend fun importImagesAsNewProject(uris: List<Uri>): Result<ScanProject> =
        withContext(Dispatchers.IO) {
            if (uris.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("No images selected"))
            }

            val projectId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val importedPages = mutableListOf<PageEntity>()

            try {
                uris.forEachIndexed { index, uri ->
                    val asset = fileManager.copyUriToImmutableOriginal(projectId, uri)
                    importedPages += PageEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        pageOrder = index,
                        displayName = asset.displayName,
                        originalFilePath = asset.originalFile.absolutePath,
                        thumbnailFilePath = asset.thumbnailFile?.absolutePath,
                        widthPx = asset.widthPx,
                        heightPx = asset.heightPx,
                        fileSizeBytes = asset.fileSizeBytes,
                        mimeType = asset.mimeType,
                        createdAtEpochMs = now,
                    )
                }

                val defaultName = buildDefaultProjectName(now)
                val projectEntity = ProjectEntity(
                    id = projectId,
                    name = defaultName,
                    createdAtEpochMs = now,
                    updatedAtEpochMs = now,
                )

                projectDao.insertProject(projectEntity)
                projectDao.insertPages(importedPages)

                val created = ProjectWithPages(projectEntity, importedPages).toDomain()
                Result.success(created)
            } catch (t: Throwable) {
                fileManager.deleteProjectFiles(projectId)
                Result.failure(t)
            }
        }

    override suspend fun addImagesToProject(projectId: String, uris: List<Uri>): Result<Int> =
        withContext(Dispatchers.IO) {
            if (uris.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("No images selected"))
            }

            val existing = projectDao.getProjectWithPages(projectId)
                ?: return@withContext Result.failure(IllegalStateException("Project no longer exists"))

            val startOrder = projectDao.getMaxPageOrder(existing.project.id) + 1
            val now = System.currentTimeMillis()
            val newPages = mutableListOf<PageEntity>()

            try {
                uris.forEachIndexed { idx, uri ->
                    val asset = fileManager.copyUriToImmutableOriginal(projectId, uri)
                    newPages += PageEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        pageOrder = startOrder + idx,
                        displayName = asset.displayName,
                        originalFilePath = asset.originalFile.absolutePath,
                        thumbnailFilePath = asset.thumbnailFile?.absolutePath,
                        widthPx = asset.widthPx,
                        heightPx = asset.heightPx,
                        fileSizeBytes = asset.fileSizeBytes,
                        mimeType = asset.mimeType,
                        createdAtEpochMs = now,
                    )
                }

                projectDao.insertPages(newPages)
                projectDao.touchProject(projectId, now)
                Result.success(newPages.size)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }

    override suspend fun renameProject(projectId: String, newName: String) {
        val cleaned = newName.trim()
        if (cleaned.isEmpty()) return
        projectDao.renameProject(projectId, cleaned, System.currentTimeMillis())
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
        fileManager.deleteProjectFiles(projectId)
    }

    override suspend fun getStorageUsage(): StorageUsageSummary {
        return fileManager.calculateStorageUsage()
    }

    override suspend fun clearPreviewCache() {
        fileManager.clearPreviewCache()
    }

    private fun buildDefaultProjectName(epochMs: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        return "Scan ${formatter.format(Date(epochMs))}"
    }

    private fun ProjectWithPages.toDomain(): ScanProject {
        val orderedPages = pages.sortedBy { it.pageOrder }.map { entity ->
            ScanPage(
                id = entity.id,
                projectId = entity.projectId,
                pageOrder = entity.pageOrder,
                displayName = entity.displayName,
                originalFilePath = entity.originalFilePath,
                thumbnailFilePath = entity.thumbnailFilePath,
                widthPx = entity.widthPx,
                heightPx = entity.heightPx,
                fileSizeBytes = entity.fileSizeBytes,
                mimeType = entity.mimeType,
                createdAtEpochMs = entity.createdAtEpochMs,
            )
        }
        val coverPath = orderedPages.firstOrNull()?.thumbnailFilePath
            ?: orderedPages.firstOrNull()?.originalFilePath

        return ScanProject(
            id = project.id,
            name = project.name,
            createdAtEpochMs = project.createdAtEpochMs,
            updatedAtEpochMs = project.updatedAtEpochMs,
            pageCount = orderedPages.size,
            coverThumbnailPath = coverPath,
            pages = orderedPages,
        )
    }
}
