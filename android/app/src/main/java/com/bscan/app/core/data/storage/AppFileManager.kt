package com.bscan.app.core.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import com.bscan.app.core.model.StorageUsageSummary
import com.bscan.app.processing.ImageInspector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class AppFileManager(private val context: Context) {

    private val projectsRootDir: File
        get() = File(context.filesDir, "projects").apply { mkdirs() }

    private val previewCacheDir: File
        get() = File(context.cacheDir, "previews").apply { mkdirs() }

    data class ImportedImageAsset(
        val displayName: String,
        val originalFile: File,
        val thumbnailFile: File?,
        val widthPx: Int,
        val heightPx: Int,
        val fileSizeBytes: Long,
        val mimeType: String,
    )

    suspend fun copyUriToImmutableOriginal(
        projectId: String,
        sourceUri: Uri,
    ): ImportedImageAsset = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(sourceUri) ?: "image/jpeg"
        val displayName = queryDisplayName(sourceUri) ?: "Imported page"
        val extension = extensionForMimeType(mimeType)

        val projectDir = File(projectsRootDir, projectId).apply { mkdirs() }
        val originalsDir = File(projectDir, "originals").apply { mkdirs() }
        val thumbnailsDir = File(projectDir, "thumbnails").apply { mkdirs() }

        val uniqueId = UUID.randomUUID().toString()
        val tempFile = File(originalsDir, ".$uniqueId.tmp")
        val targetFile = File(originalsDir, "$uniqueId.$extension")

        try {
            val inputStream = resolver.openInputStream(sourceUri)
                ?: throw IOException("Unable to open image stream for URI")

            inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        output.write(buffer, 0, read)
                    }
                    output.fd.sync()
                }
            }

            if (tempFile.length() <= 0L) {
                throw IOException("Selected file is empty")
            }

            val dimensions = ImageInspector.inspect(tempFile)
            if (dimensions.widthPx <= 0 || dimensions.heightPx <= 0) {
                throw IOException("Selected file is not a supported bitmap image")
            }

            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = false)
                tempFile.delete()
            }

            targetFile.setReadOnly()

            val thumbnailFile = generateThumbnail(
                originalFile = targetFile,
                thumbnailsDir = thumbnailsDir,
                uniqueId = uniqueId,
                exifOrientation = dimensions.exifOrientation,
            )

            ImportedImageAsset(
                displayName = displayName,
                originalFile = targetFile,
                thumbnailFile = thumbnailFile,
                widthPx = dimensions.widthPx,
                heightPx = dimensions.heightPx,
                fileSizeBytes = targetFile.length(),
                mimeType = mimeType,
            )
        } catch (e: Exception) {
            tempFile.delete()
            if (targetFile.exists()) {
                targetFile.setWritable(true)
                targetFile.delete()
            }
            throw e
        }
    }

    private fun generateThumbnail(
        originalFile: File,
        thumbnailsDir: File,
        uniqueId: String,
        exifOrientation: Int,
    ): File? {
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(originalFile.absolutePath, bounds)
            val sampleSize = ImageInspector.calculateInSampleSize(
                rawWidth = bounds.outWidth,
                rawHeight = bounds.outHeight,
                maxLongEdgePx = THUMBNAIL_MAX_EDGE_PX,
            )

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            val decoded = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOptions)
                ?: return null

            val oriented = try {
                val matrix = ImageInspector.exifTransformMatrix(exifOrientation)
                if (!matrix.isIdentity) {
                    Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
                        .also { if (it !== decoded) decoded.recycle() }
                } else {
                    decoded
                }
            } catch (e: Throwable) {
                decoded.recycle()
                throw e
            }

            val thumbTemp = File(thumbnailsDir, ".$uniqueId.thumb.tmp")
            val thumbTarget = File(thumbnailsDir, "$uniqueId.jpg")
            try {
                FileOutputStream(thumbTemp).use { out ->
                    oriented.compress(Bitmap.CompressFormat.JPEG, 82, out)
                }
                if (!thumbTemp.renameTo(thumbTarget)) {
                    thumbTemp.copyTo(thumbTarget, overwrite = true)
                    thumbTemp.delete()
                }
                thumbTarget
            } finally {
                oriented.recycle()
                if (thumbTemp.exists()) thumbTemp.delete()
            }
        }.getOrNull()
    }

    suspend fun deleteProjectFiles(projectId: String) = withContext(Dispatchers.IO) {
        val projectDir = File(projectsRootDir, projectId)
        if (projectDir.exists()) {
            projectDir.walkBottomUp().forEach { file ->
                if (!file.canWrite()) {
                    file.setWritable(true)
                }
                file.delete()
            }
        }
    }

    suspend fun calculateStorageUsage(): StorageUsageSummary = withContext(Dispatchers.IO) {
        var originalsBytes = 0L
        var thumbnailsBytes = 0L

        projectsRootDir.listFiles()?.forEach { projectDir ->
            if (projectDir.isDirectory) {
                originalsBytes += directorySizeBytes(File(projectDir, "originals"))
                thumbnailsBytes += directorySizeBytes(File(projectDir, "thumbnails"))
            }
        }
        val cacheBytes = directorySizeBytes(context.cacheDir)

        StorageUsageSummary(
            originalsBytes = originalsBytes,
            thumbnailsBytes = thumbnailsBytes,
            cacheBytes = cacheBytes,
        )
    }

    suspend fun clearPreviewCache() = withContext(Dispatchers.IO) {
        previewCacheDir.listFiles()?.forEach { it.deleteRecursively() }
        context.cacheDir.listFiles()?.forEach { child ->
            if (child.name != "code_cache") {
                child.deleteRecursively()
            }
        }
    }

    private fun directorySizeBytes(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    private fun queryDisplayName(uri: Uri): String? {
        return runCatching {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else {
                    null
                }
            }
        }.getOrNull()
    }

    private fun extensionForMimeType(mimeType: String): String {
        return when (mimeType.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/heic", "image/heif" -> "heic"
            else -> "jpg"
        }
    }

    companion object {
        private const val THUMBNAIL_MAX_EDGE_PX = 420
    }
}
