package com.bscan.app.core.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.ui.icons.BscanIcons
import com.bscan.app.core.ui.theme.BscanThemeTokens

@Composable
fun SampledLocalImage(
    filePath: String?,
    maxLongEdgePx: Int,
    bitmapLoader: SampledBitmapLoader,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    preferLowMemoryConfig: Boolean = true,
) {
    var loadState by remember(filePath, maxLongEdgePx) {
        mutableStateOf<ImageLoadState>(ImageLoadState.Loading)
    }

    LaunchedEffect(filePath, maxLongEdgePx, preferLowMemoryConfig) {
        if (filePath.isNullOrBlank()) {
            loadState = ImageLoadState.Empty
            return@LaunchedEffect
        }
        loadState = ImageLoadState.Loading
        val result = runCatching {
            bitmapLoader.loadSampledBitmap(
                filePath = filePath,
                maxLongEdgePx = maxLongEdgePx,
                preferLowMemoryConfig = preferLowMemoryConfig,
            )
        }.getOrNull()

        loadState = if (result != null) {
            ImageLoadState.Loaded(result)
        } else {
            ImageLoadState.Failed
        }
    }

    val surfaceBg = BscanThemeTokens.extendedColors.surfaceElevated
    Box(
        modifier = modifier.background(surfaceBg),
        contentAlignment = Alignment.Center,
    ) {
        when (val state = loadState) {
            is ImageLoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            is ImageLoadState.Loaded -> {
                Image(
                    bitmap = state.bitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is ImageLoadState.Empty -> {
                Icon(
                    imageVector = BscanIcons.Description,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }
            is ImageLoadState.Failed -> {
                Icon(
                    imageVector = BscanIcons.BrokenImage,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

private sealed interface ImageLoadState {
    data object Loading : ImageLoadState
    data object Empty : ImageLoadState
    data object Failed : ImageLoadState
    data class Loaded(val bitmap: Bitmap) : ImageLoadState
}
