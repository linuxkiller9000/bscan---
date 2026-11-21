package com.bscan.app.feature.home

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bscan.app.R
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.ui.components.BscanEmptyState
import com.bscan.app.core.ui.components.BscanErrorBanner
import com.bscan.app.core.ui.components.BscanLoadingRow
import com.bscan.app.core.ui.components.BscanWordmark
import com.bscan.app.core.ui.components.ProjectListItemCard
import com.bscan.app.core.ui.icons.BscanIcons
import com.bscan.app.core.ui.theme.BscanThemeTokens
import com.bscan.app.feature.importmedia.rememberGalleryImagePicker

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    bitmapLoader: SampledBitmapLoader,
    onScanDocumentClick: () -> Unit,
    onOpenProject: (String) -> Unit,
    onViewAllProjects: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val importFallbackError = stringResource(R.string.preview_import_error)

    val launchGalleryPicker = rememberGalleryImagePicker(
        onImagesPicked = { uris: List<Uri> ->
            viewModel.importImages(
                uris = uris,
                fallbackErrorMessage = importFallbackError,
                onProjectCreated = onOpenProject,
            )
        },
        onPickerUnavailable = {
            viewModel.reportError(importFallbackError)
        },
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "header") {
            BscanWordmark()
        }

        if (uiState.errorMessage != null) {
            item(key = "error") {
                BscanErrorBanner(
                    message = uiState.errorMessage!!,
                    onDismiss = viewModel::dismissError,
                )
            }
        }

        if (uiState.isImporting) {
            item(key = "importing") {
                BscanLoadingRow(label = stringResource(R.string.preview_importing))
            }
        }

        item(key = "primary_actions") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    onClick = onScanDocumentClick,
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = BscanIcons.DocumentScanner,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.action_scan_document),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.action_scan_document_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f),
                            )
                        }
                    }
                }

                Surface(
                    onClick = { if (!uiState.isImporting) launchGalleryPicker() },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BscanThemeTokens.extendedColors.borderStrong),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = BscanIcons.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.action_import_photos),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.action_import_photos_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        item(key = "recent_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.section_recent_projects),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                if (uiState.recentProjects.isNotEmpty()) {
                    TextButton(onClick = onViewAllProjects) {
                        Text(text = stringResource(R.string.action_view_all))
                    }
                }
            }
        }

        if (uiState.recentProjects.isEmpty()) {
            item(key = "empty_recent") {
                BscanEmptyState(
                    title = stringResource(R.string.empty_home_title),
                    body = stringResource(R.string.empty_home_body),
                    icon = BscanIcons.Folder,
                    action = {
                        OutlinedButton(
                            onClick = { if (!uiState.isImporting) launchGalleryPicker() },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, BscanThemeTokens.extendedColors.borderStrong),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Icon(
                                imageVector = BscanIcons.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_import_photos))
                        }
                    },
                )
            }
        } else {
            items(
                items = uiState.recentProjects,
                key = { it.id },
            ) { project ->
                ProjectListItemCard(
                    project = project,
                    bitmapLoader = bitmapLoader,
                    onClick = { onOpenProject(project.id) },
                )
            }
        }
    }
}
