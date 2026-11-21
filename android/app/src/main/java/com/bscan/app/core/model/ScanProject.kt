package com.bscan.app.core.model

data class ScanProject(
    val id: String,
    val name: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val pageCount: Int,
    val coverThumbnailPath: String?,
    val pages: List<ScanPage> = emptyList(),
)

data class ScanPage(
    val id: String,
    val projectId: String,
    val pageOrder: Int,
    val displayName: String,
    val originalFilePath: String,
    val thumbnailFilePath: String?,
    val widthPx: Int,
    val heightPx: Int,
    val fileSizeBytes: Long,
    val mimeType: String,
    val createdAtEpochMs: Long,
)
