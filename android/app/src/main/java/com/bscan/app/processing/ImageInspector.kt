package com.bscan.app.processing

import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File

data class ImageDimensions(
    val widthPx: Int,
    val heightPx: Int,
    val exifOrientation: Int,
)

object ImageInspector {

    fun inspect(file: File): ImageDimensions {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        val rawWidth = options.outWidth.coerceAtLeast(0)
        val rawHeight = options.outHeight.coerceAtLeast(0)

        val orientation = runCatching {
            ExifInterface(file.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

        val rotated90 = orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
            orientation == ExifInterface.ORIENTATION_ROTATE_270 ||
            orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
            orientation == ExifInterface.ORIENTATION_TRANSVERSE

        return ImageDimensions(
            widthPx = if (rotated90) rawHeight else rawWidth,
            heightPx = if (rotated90) rawWidth else rawHeight,
            exifOrientation = orientation,
        )
    }

    fun calculateInSampleSize(
        rawWidth: Int,
        rawHeight: Int,
        maxLongEdgePx: Int,
    ): Int {
        if (rawWidth <= 0 || rawHeight <= 0 || maxLongEdgePx <= 0) return 1
        var sampleSize = 1
        val longEdge = maxOf(rawWidth, rawHeight)
        while ((longEdge / (sampleSize * 2)) >= maxLongEdgePx) {
            sampleSize *= 2
        }
        return sampleSize.coerceAtLeast(1)
    }

    fun exifTransformMatrix(exifOrientation: Int): Matrix {
        val matrix = Matrix()
        when (exifOrientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        }
        return matrix
    }
}
