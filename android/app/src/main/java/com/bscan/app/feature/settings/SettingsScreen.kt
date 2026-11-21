package com.bscan.app.feature.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bscan.app.BuildConfig
import com.bscan.app.R
import com.bscan.app.core.model.PreviewResolutionCap
import com.bscan.app.core.ui.components.BscanCard
import com.bscan.app.core.ui.components.BscanFormatters
import com.bscan.app.core.ui.components.StatusTag
import com.bscan.app.core.ui.theme.BscanThemeTokens

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val extended = BscanThemeTokens.extendedColors
    val cacheClearedMsg = stringResource(R.string.settings_cache_cleared)

    LaunchedEffect(Unit) {
        viewModel.refreshStorageUsage()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "header") {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        if (uiState.statusMessage != null) {
            item(key = "status") {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = extended.successContainer,
                    border = BorderStroke(1.dp, extended.success.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = extended.success,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = uiState.statusMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        item(key = "ocr_section") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = stringResource(R.string.settings_section_ocr),
                    icon = Icons.Outlined.Translate,
                )
                BscanCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = stringResource(R.string.settings_ocr_summary),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.ocrLanguages.forEachIndexed { idx, lang ->
                            if (idx > 0) {
                                HorizontalDivider(
                                    color = extended.borderSubtle,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${lang.englishName} · ${lang.nativeName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = buildString {
                                            append("Code: ${lang.tesseractCode}")
                                            if (lang.isRtl) append(" · RTL")
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = extended.textMuted,
                                    )
                                }
                                StatusTag(
                                    label = stringResource(R.string.settings_ocr_not_installed),
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "processing_section") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = stringResource(R.string.settings_section_processing),
                    icon = Icons.Outlined.Memory,
                )
                BscanCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    text = stringResource(R.string.settings_processing_sequential),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.settings_processing_sequential_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = uiState.processingPreferences.sequentialSinglePageProcessing,
                                onCheckedChange = viewModel::setSequentialSinglePageProcessing,
                            )
                        }

                        HorizontalDivider(color = extended.borderSubtle)

                        Text(
                            text = stringResource(R.string.settings_preview_quality),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.settings_preview_quality_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            PreviewResolutionCap.entries.forEach { cap ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = uiState.processingPreferences.previewResolutionCap == cap,
                                        onClick = { viewModel.setPreviewResolutionCap(cap) },
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cap.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "storage_section") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = stringResource(R.string.settings_section_storage),
                    icon = Icons.Outlined.Storage,
                )
                BscanCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        StorageRow(
                            label = stringResource(R.string.settings_storage_originals),
                            value = BscanFormatters.formatByteCount(
                                context,
                                uiState.storageUsage.originalsBytes,
                            ),
                        )
                        StorageRow(
                            label = stringResource(R.string.settings_storage_thumbnails),
                            value = BscanFormatters.formatByteCount(
                                context,
                                uiState.storageUsage.thumbnailsBytes,
                            ),
                        )
                        StorageRow(
                            label = stringResource(R.string.settings_storage_cache),
                            value = BscanFormatters.formatByteCount(
                                context,
                                uiState.storageUsage.cacheBytes,
                            ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { viewModel.clearPreviewCache(cacheClearedMsg) },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, extended.borderStrong),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CleaningServices,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.settings_clear_cache))
                        }
                    }
                }
            }
        }

        item(key = "about_section") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = stringResource(R.string.settings_section_about),
                    icon = Icons.Outlined.Shield,
                )
                BscanCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.settings_privacy_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.settings_privacy_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        HorizontalDivider(
                            color = extended.borderSubtle,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                        StorageRow(
                            label = stringResource(R.string.settings_version_label),
                            value = BuildConfig.VERSION_NAME,
                        )
                        StorageRow(
                            label = stringResource(R.string.settings_app_id_label),
                            value = BuildConfig.APPLICATION_ID,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun StorageRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
