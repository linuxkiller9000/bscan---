package com.bscan.app.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight 24dp vector icons for bscan, avoiding the ~25 MB material-icons-extended artifact.
 */
object BscanIcons {

    val DocumentScanner: ImageVector by lazy {
        strokeIcon("DocumentScanner") {
            // Corner brackets
            moveTo(3f, 7f); verticalLineTo(4f); horizontalLineTo(7f)
            moveTo(17f, 4f); horizontalLineTo(21f); verticalLineTo(7f)
            moveTo(3f, 17f); verticalLineTo(20f); horizontalLineTo(7f)
            moveTo(17f, 20f); horizontalLineTo(21f); verticalLineTo(17f)
            // Document frame + scan line
            moveTo(7f, 7f); horizontalLineTo(17f); verticalLineTo(17f); horizontalLineTo(7f); close()
            moveTo(5f, 12f); horizontalLineTo(19f)
        }
    }

    val PhotoLibrary: ImageVector by lazy {
        strokeIcon("PhotoLibrary") {
            moveTo(4f, 6f); horizontalLineTo(18f); verticalLineTo(18f); horizontalLineTo(4f); close()
            moveTo(7f, 14f); lineTo(10f, 11f); lineTo(13f, 14f); lineTo(15f, 12f); lineTo(17f, 15f)
            moveTo(8f, 21f); horizontalLineTo(20f); verticalLineTo(9f)
        }
    }

    val Folder: ImageVector by lazy {
        strokeIcon("Folder") {
            moveTo(3f, 6f); horizontalLineTo(9.5f); lineTo(11.5f, 8.5f); horizontalLineTo(21f)
            verticalLineTo(18.5f); horizontalLineTo(3f); close()
        }
    }

    val Camera: ImageVector by lazy {
        strokeIcon("Camera") {
            moveTo(3f, 8f); horizontalLineTo(7f); lineTo(9f, 5.5f); horizontalLineTo(15f)
            lineTo(17f, 8f); horizontalLineTo(21f); verticalLineTo(19f); horizontalLineTo(3f); close()
            moveTo(12f, 10f); horizontalLineTo(12.01f)
            moveTo(9f, 13.5f); horizontalLineTo(15f); verticalLineTo(13.5f)
            moveTo(12f, 10.5f); lineTo(15f, 13.5f); lineTo(12f, 16.5f); lineTo(9f, 13.5f); close()
        }
    }

    val Shield: ImageVector by lazy {
        strokeIcon("Shield") {
            moveTo(12f, 3f); lineTo(19f, 6f); verticalLineTo(12f)
            lineTo(16.5f, 17.5f); lineTo(12f, 21f); lineTo(7.5f, 17.5f)
            lineTo(5f, 12f); verticalLineTo(6f); close()
        }
    }

    val VerifiedShield: ImageVector by lazy {
        strokeIcon("VerifiedShield") {
            moveTo(12f, 3f); lineTo(19f, 6f); verticalLineTo(12f)
            lineTo(16.5f, 17.5f); lineTo(12f, 21f); lineTo(7.5f, 17.5f)
            lineTo(5f, 12f); verticalLineTo(6f); close()
            moveTo(9f, 12f); lineTo(11.2f, 14.2f); lineTo(15.5f, 9.8f)
        }
    }

    val Description: ImageVector by lazy {
        strokeIcon("Description") {
            moveTo(6f, 3f); horizontalLineTo(14f); lineTo(18f, 7f); verticalLineTo(21f); horizontalLineTo(6f); close()
            moveTo(14f, 3f); verticalLineTo(7f); horizontalLineTo(18f)
            moveTo(9f, 12f); horizontalLineTo(15f)
            moveTo(9f, 16f); horizontalLineTo(14f)
        }
    }

    val BrokenImage: ImageVector by lazy {
        strokeIcon("BrokenImage") {
            moveTo(4f, 5f); horizontalLineTo(20f); verticalLineTo(19f); horizontalLineTo(4f); close()
            moveTo(4f, 14f); lineTo(9f, 10f); lineTo(12f, 13f); lineTo(16f, 9f); lineTo(20f, 13f)
            moveTo(4f, 5f); lineTo(20f, 19f)
        }
    }

    val Crop: ImageVector by lazy {
        strokeIcon("Crop") {
            moveTo(7f, 3f); verticalLineTo(17f); horizontalLineTo(21f)
            moveTo(3f, 7f); horizontalLineTo(17f); verticalLineTo(21f)
        }
    }

    val RotateRight: ImageVector by lazy {
        strokeIcon("RotateRight") {
            moveTo(12f, 5f); horizontalLineTo(17f); verticalLineTo(10f)
            moveTo(17f, 5f); lineTo(14f, 8f)
            moveTo(18f, 13f); verticalLineTo(17f); horizontalLineTo(6f); verticalLineTo(7f); horizontalLineTo(10f)
        }
    }

    val Perspective: ImageVector by lazy {
        strokeIcon("Perspective") {
            moveTo(6f, 5f); lineTo(18f, 3.5f); lineTo(20f, 20.5f); lineTo(4f, 19f); close()
        }
    }

    val Dewarp: ImageVector by lazy {
        strokeIcon("Dewarp") {
            moveTo(4f, 5f); horizontalLineTo(20f); verticalLineTo(19f); horizontalLineTo(4f); close()
            moveTo(9.5f, 5f); verticalLineTo(19f)
            moveTo(14.5f, 5f); verticalLineTo(19f)
            moveTo(4f, 12f); horizontalLineTo(20f)
        }
    }

    val Enhance: ImageVector by lazy {
        strokeIcon("Enhance") {
            moveTo(12f, 3f); lineTo(13.8f, 8.2f); lineTo(19f, 10f); lineTo(13.8f, 11.8f)
            lineTo(12f, 17f); lineTo(10.2f, 11.8f); lineTo(5f, 10f); lineTo(10.2f, 8.2f); close()
            moveTo(5f, 19f); lineTo(9f, 15f)
        }
    }

    val TextOcr: ImageVector by lazy {
        strokeIcon("TextOcr") {
            moveTo(5f, 6f); horizontalLineTo(19f)
            moveTo(12f, 6f); verticalLineTo(19f)
            moveTo(9f, 19f); horizontalLineTo(15f)
        }
    }

    val Export: ImageVector by lazy {
        strokeIcon("Export") {
            moveTo(12f, 15f); verticalLineTo(4f)
            moveTo(8f, 8f); lineTo(12f, 4f); lineTo(16f, 8f)
            moveTo(4f, 15f); verticalLineTo(19f); horizontalLineTo(20f); verticalLineTo(15f)
        }
    }

    val Translate: ImageVector by lazy {
        strokeIcon("Translate") {
            moveTo(4f, 6f); horizontalLineTo(13f)
            moveTo(8.5f, 4f); verticalLineTo(6f)
            moveTo(5.5f, 15f); lineTo(11f, 6f)
            moveTo(6f, 9f); lineTo(11.5f, 14f)
            moveTo(13f, 20f); lineTo(16.5f, 11f); lineTo(20f, 20f)
            moveTo(14.2f, 17f); horizontalLineTo(18.8f)
        }
    }

    val Memory: ImageVector by lazy {
        strokeIcon("Memory") {
            moveTo(7f, 7f); horizontalLineTo(17f); verticalLineTo(17f); horizontalLineTo(7f); close()
            moveTo(10f, 3f); verticalLineTo(7f)
            moveTo(14f, 3f); verticalLineTo(7f)
            moveTo(10f, 17f); verticalLineTo(21f)
            moveTo(14f, 17f); verticalLineTo(21f)
            moveTo(3f, 10f); horizontalLineTo(7f)
            moveTo(3f, 14f); horizontalLineTo(7f)
            moveTo(17f, 10f); horizontalLineTo(21f)
            moveTo(17f, 14f); horizontalLineTo(21f)
        }
    }

    val Storage: ImageVector by lazy {
        strokeIcon("Storage") {
            moveTo(4f, 5f); horizontalLineTo(20f); verticalLineTo(10f); horizontalLineTo(4f); close()
            moveTo(4f, 14f); horizontalLineTo(20f); verticalLineTo(19f); horizontalLineTo(4f); close()
            moveTo(7f, 7.5f); horizontalLineTo(8f)
            moveTo(7f, 16.5f); horizontalLineTo(8f)
        }
    }

    val CleanCache: ImageVector by lazy {
        strokeIcon("CleanCache") {
            moveTo(5f, 19f); horizontalLineTo(19f)
            moveTo(8f, 19f); lineTo(10f, 11f); horizontalLineTo(14f); lineTo(16f, 19f)
            moveTo(12f, 4f); verticalLineTo(11f)
        }
    }

    private fun strokeIcon(
        name: String,
        build: PathBuilder.() -> Unit,
    ): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathBuilder = build,
            )
        }.build()
    }
}
