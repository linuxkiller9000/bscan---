package com.bscan.app.core.model

enum class PreviewResolutionCap(val maxLongEdgePx: Int, val label: String) {
    BALANCED(1600, "Balanced (1600 px)"),
    CONSERVATIVE(1200, "Low memory (1200 px)"),
    HIGH(2048, "High detail (2048 px)"),
}

data class ProcessingPreferences(
    val sequentialSinglePageProcessing: Boolean = true,
    val previewResolutionCap: PreviewResolutionCap = PreviewResolutionCap.BALANCED,
)

data class StorageUsageSummary(
    val originalsBytes: Long = 0L,
    val thumbnailsBytes: Long = 0L,
    val cacheBytes: Long = 0L,
) {
    val totalBytes: Long
        get() = originalsBytes + thumbnailsBytes + cacheBytes
}
