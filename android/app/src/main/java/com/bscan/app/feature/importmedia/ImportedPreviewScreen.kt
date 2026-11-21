package com.bscan.app.feature.importmedia

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bscan.app.R
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.model.ScanPage
import com.bscan.app.core.ui.components.BscanCard
import com.bscan.app.core.ui.components.BscanEmptyState
import com.bscan.app.core.ui.components.BscanErrorBanner
import com.bscan.app.core.ui.components.BscanFormatters
import com.bscan.app.core.ui.components.BscanLoadingRow
import com.bscan.app.core.ui.components.SampledLocalImage
import com.bscan.app.core.ui.components.StatusTag
import com.bscan.app.core.ui.icons.BscanIcons
import com.bscan.app.core.ui.theme.BscanThemeTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportedPreviewScreen(
    viewModel: ImportedPreviewViewModel,
    bitmapLoader: SampledBitmapLoader,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val extended = BscanThemeTokens.extendedColors
    val importFallbackError = stringResource(R.string.preview_import_error)

    val launchAddPagesPicker = rememberGalleryImagePicker(
        onImagesPicked = { uris ->
            viewModel.addPages(uris = uris, fallbackErrorMessage = importFallbackError)
        },
    )

    val project = uiState.project
    val currentPage: ScanPage? = project?.pages?.getOrNull(uiState.selectedPageIndex)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = project?.name ?: stringResource(R.string.preview_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        if (project != null && project.pages.isNotEmpty()) {
                            Text(
                                text = stringResource(
                                    R.string.preview_page_indicator,
                                    uiState.selectedPageIndex + 1,
                                    project.pages.size,
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back),
                        )
                    }
                },
                actions = {
                    if (project != null) {
                        TextButton(
                            enabled = !uiState.isAddingPages,
                            onClick = { launchAddPagesPicker() },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.preview_add_pages))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            project == null || currentPage == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BscanEmptyState(
                        title = stringResource(R.string.preview_load_error),
                        body = stringResource(R.string.projects_empty_body),
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (uiState.errorMessage != null) {
                        BscanErrorBanner(
                            message = uiState.errorMessage!!,
                            onDismiss = viewModel::dismissError,
                        )
                    }

                    if (uiState.isAddingPages) {
                        BscanLoadingRow(label = stringResource(R.string.preview_importing))
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, extended.borderSubtle),
                    ) {
                        SampledLocalImage(
                            filePath = currentPage.originalFilePath,
                            maxLongEdgePx = uiState.previewMaxEdgePx,
                            bitmapLoader = bitmapLoader,
                            contentDescription = currentPage.displayName,
                            contentScale = ContentScale.Fit,
                            preferLowMemoryConfig = false,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                        )
                    }

                    if (project.pages.size > 1) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 2.dp),
                        ) {
                            itemsIndexed(
                                items = project.pages,
                                key = { _, page -> page.id },
                            ) { index, page ->
                                val isSelected = index == uiState.selectedPageIndex
                                Surface(
                                    onClick = { viewModel.selectPage(index) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = extended.surfaceElevated,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            extended.borderSubtle
                                        },
                                    ),
                                    modifier = Modifier.size(width = 58.dp, height = 74.dp),
                                ) {
                                    SampledLocalImage(
                                        filePath = page.thumbnailFilePath ?: page.originalFilePath,
                                        maxLongEdgePx = 220,
                                        bitmapLoader = bitmapLoader,
                                        contentDescription = page.displayName,
                                    )
                                }
                            }
                        }
                    }

                    BscanCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = extended.success,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = stringResource(R.string.preview_original_preserved),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = extended.success,
                                )
                            }
                            Text(
                                text = currentPage.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                MetadataPair(
                                    label = stringResource(R.string.preview_dimensions_label),
                                    value = "${currentPage.widthPx} × ${currentPage.heightPx} px",
                                )
                                MetadataPair(
                                    label = stringResource(R.string.preview_size_label),
                                    value = BscanFormatters.formatByteCount(
                                        context,
                                        currentPage.fileSizeBytes,
                                    ),
                                )
                            }
                        }
                    }

                    BscanCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.preview_tools_section),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                StatusTag(label = stringResource(R.string.badge_not_implemented))
                            }
                            Text(
                                text = stringResource(R.string.preview_tools_milestone_note),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                item { DisabledToolTile(stringResource(R.string.tool_crop), BscanIcons.Crop) }
                                item { DisabledToolTile(stringResource(R.string.tool_rotate), BscanIcons.RotateRight) }
                                item { DisabledToolTile(stringResource(R.string.tool_perspective), BscanIcons.Perspective) }
                                item { DisabledToolTile(stringResource(R.string.tool_dewarp), BscanIcons.Dewarp) }
                                item { DisabledToolTile(stringResource(R.string.tool_enhance), BscanIcons.Enhance) }
                                item { DisabledToolTile(stringResource(R.string.tool_ocr), BscanIcons.TextOcr) }
                                item { DisabledToolTile(stringResource(R.string.tool_export), BscanIcons.Export) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun MetadataPair(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = BscanThemeTokens.extendedColors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DisabledToolTile(
    label: String,
    icon: ImageVector,
) {
    val extended = BscanThemeTokens.extendedColors
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = extended.surfaceElevated.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, extended.borderSubtle),
    ) {
        Column(
            modifier = Modifier
                .width(78.dp)
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = extended.textMuted,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = extended.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
