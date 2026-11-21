package com.bscan.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bscan.app.R
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.model.ScanProject
import com.bscan.app.core.ui.theme.BscanThemeTokens

@Composable
fun ProjectListItemCard(
    project: ScanProject,
    bitmapLoader: SampledBitmapLoader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRenameClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val extended = BscanThemeTokens.extendedColors
    var menuExpanded by remember { mutableStateOf(false) }

    BscanCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier
                    .size(width = 52.dp, height = 66.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, extended.borderSubtle),
                color = extended.surfaceElevated,
            ) {
                SampledLocalImage(
                    filePath = project.coverThumbnailPath,
                    maxLongEdgePx = 300,
                    bitmapLoader = bitmapLoader,
                    contentDescription = project.name,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = BscanFormatters.formatPageCount(context, project.pageCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = BscanFormatters.formatDate(project.updatedAtEpochMs),
                    style = MaterialTheme.typography.bodySmall,
                    color = extended.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (onRenameClick != null || onDeleteClick != null) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = extended.surfaceElevated,
                    ) {
                        if (onRenameClick != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.action_rename_project)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.DriveFileRenameOutline,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRenameClick()
                                },
                            )
                        }
                        if (onDeleteClick != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.action_delete_project),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                },
                            )
                        }
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = extended.textMuted,
                )
            }
        }
    }
}
