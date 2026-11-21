package com.bscan.app.core.model

data class OcrLanguage(
    val tesseractCode: String,
    val englishName: String,
    val nativeName: String,
    val isRtl: Boolean,
    val isInstalled: Boolean,
)

object SupportedOcrLanguages {
    val initialCatalog: List<OcrLanguage> = listOf(
        OcrLanguage("eng", "English", "English", isRtl = false, isInstalled = false),
        OcrLanguage("ara", "Arabic", "العربية", isRtl = true, isInstalled = false),
        OcrLanguage("fra", "French", "Français", isRtl = false, isInstalled = false),
        OcrLanguage("deu", "German", "Deutsch", isRtl = false, isInstalled = false),
        OcrLanguage("spa", "Spanish", "Español", isRtl = false, isInstalled = false),
        OcrLanguage("ita", "Italian", "Italiano", isRtl = false, isInstalled = false),
        OcrLanguage("tur", "Turkish", "Türkçe", isRtl = false, isInstalled = false),
    )
}
