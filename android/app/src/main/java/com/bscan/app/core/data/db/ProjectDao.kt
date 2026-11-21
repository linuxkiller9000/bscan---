package com.bscan.app.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Transaction
    @Query("SELECT * FROM projects ORDER BY updated_at_epoch_ms DESC")
    fun observeAllProjectsWithPages(): Flow<List<ProjectWithPages>>

    @Transaction
    @Query("SELECT * FROM projects ORDER BY updated_at_epoch_ms DESC LIMIT :limit")
    fun observeRecentProjectsWithPages(limit: Int): Flow<List<ProjectWithPages>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun observeProjectWithPages(projectId: String): Flow<ProjectWithPages?>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectWithPages(projectId: String): ProjectWithPages?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPages(pages: List<PageEntity>)

    @Query("SELECT COALESCE(MAX(page_order), -1) FROM pages WHERE project_id = :projectId")
    suspend fun getMaxPageOrder(projectId: String): Int

    @Query("UPDATE projects SET name = :newName, updated_at_epoch_ms = :updatedAtEpochMs WHERE id = :projectId")
    suspend fun renameProject(projectId: String, newName: String, updatedAtEpochMs: Long)

    @Query("UPDATE projects SET updated_at_epoch_ms = :updatedAtEpochMs WHERE id = :projectId")
    suspend fun touchProject(projectId: String, updatedAtEpochMs: Long)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
}
