package com.bscan.app.core.data.storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import com.bscan.app.processing.ImageInspector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SampledBitmapLoader {

    private val memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(CACHE_SIZE_KB) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.allocationByteCount / 1024).coerceAtLeast(1)
        }
    }

    suspend fun loadSampledBitmap(
        filePath: String,
        maxLongEdgePx: Int,
        preferLowMemoryConfig: Boolean = false,
    ): Bitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "$filePath#$maxLongEdgePx#$preferLowMemoryConfig"
        memoryCache.get(cacheKey)?.let { cached ->
            if (!cached.isRecycled) return@withContext cached
        }

        val file = File(filePath)
        if (!file.exists() || !file.isFile) return@withContext null

        val info = ImageInspector.inspect(file)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

        val sampleSize = ImageInspector.calculateInSampleSize(
            rawWidth = bounds.outWidth,
            rawHeight = bounds.outHeight,
            maxLongEdgePx = maxLongEdgePx,
        )

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = if (preferLowMemoryConfig) {
                Bitmap.Config.RGB_565
            } else {
                Bitmap.Config.ARGB_8888
            }
        }

        val decoded = BitmapFactory.decodeFile(file.absolutePath, options) ?: return@withContext null

        val finalBitmap = try {
            val matrix = ImageInspector.exifTransformMatrix(info.exifOrientation)
            if (!matrix.isIdentity) {
                Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
                    .also { if (it !== decoded) decoded.recycle() }
            } else {
                decoded
            }
        } catch (t: Throwable) {
            decoded.recycle()
            throw t
        }

        memoryCache.put(cacheKey, finalBitmap)
        finalBitmap
    }

    fun clearMemoryCache() {
        memoryCache.evictAll()
    }

    companion object {
        private const val CACHE_SIZE_KB = 16 * 1024
    }
}
