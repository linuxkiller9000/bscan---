package com.bscan.app.feature.projects

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bscan.app.R
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.model.ScanProject
import com.bscan.app.core.ui.components.BscanEmptyState
import com.bscan.app.core.ui.components.BscanErrorBanner
import com.bscan.app.core.ui.components.BscanLoadingRow
import com.bscan.app.core.ui.components.ProjectListItemCard
import com.bscan.app.core.ui.icons.BscanIcons
import com.bscan.app.core.ui.theme.BscanThemeTokens
import com.bscan.app.feature.importmedia.rememberGalleryImagePicker

@Composable
fun ProjectsScreen(
    viewModel: ProjectsViewModel,
    bitmapLoader: SampledBitmapLoader,
    onOpenProject: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val importFallbackError = stringResource(R.string.preview_import_error)

    var projectPendingDelete by remember { mutableStateOf<ScanProject?>(null) }
    var projectPendingRename by remember { mutableStateOf<ScanProject?>(null) }

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
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "title_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.projects_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                FilledTonalButton(
                    onClick = { if (!uiState.isImporting) launchGalleryPicker() },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.action_import_photos))
                }
            }
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

        if (uiState.projects.isEmpty()) {
            item(key = "empty") {
                BscanEmptyState(
                    title = stringResource(R.string.projects_empty_title),
                    body = stringResource(R.string.projects_empty_body),
                    icon = BscanIcons.Folder,
                    action = {
                        Button(
                            onClick = { if (!uiState.isImporting) launchGalleryPicker() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(text = stringResource(R.string.action_import_photos))
                        }
                    },
                )
            }
        } else {
            items(
                items = uiState.projects,
                key = { it.id },
            ) { project ->
                ProjectListItemCard(
                    project = project,
                    bitmapLoader = bitmapLoader,
                    onClick = { onOpenProject(project.id) },
                    onRenameClick = { projectPendingRename = project },
                    onDeleteClick = { projectPendingDelete = project },
                )
            }
        }
    }

    projectPendingDelete?.let { project ->
        AlertDialog(
            onDismissRequest = { projectPendingDelete = null },
            containerColor = BscanThemeTokens.extendedColors.surfaceElevated,
            title = { Text(stringResource(R.string.dialog_delete_title)) },
            text = { Text(stringResource(R.string.dialog_delete_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject(project.id)
                        projectPendingDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { projectPendingDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    projectPendingRename?.let { project ->
        var text by remember(project.id) { mutableStateOf(project.name) }
        AlertDialog(
            onDismissRequest = { projectPendingRename = null },
            containerColor = BscanThemeTokens.extendedColors.surfaceElevated,
            title = { Text(stringResource(R.string.dialog_rename_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text(stringResource(R.string.dialog_rename_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = text.isNotBlank(),
                    onClick = {
                        viewModel.renameProject(project.id, text)
                        projectPendingRename = null
                    },
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { projectPendingRename = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}
