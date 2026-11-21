package com.bscan.app.core.data.preferences

import android.content.Context
import com.bscan.app.core.model.PreviewResolutionCap
import com.bscan.app.core.model.ProcessingPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(readPreferences())
    val preferences: StateFlow<ProcessingPreferences> = _preferences.asStateFlow()

    fun setSequentialSinglePageProcessing(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SEQUENTIAL_SINGLE_PAGE, enabled).apply()
        _preferences.value = readPreferences()
    }

    fun setPreviewResolutionCap(cap: PreviewResolutionCap) {
        prefs.edit().putString(KEY_PREVIEW_CAP, cap.name).apply()
        _preferences.value = readPreferences()
    }

    private fun readPreferences(): ProcessingPreferences {
        val sequential = prefs.getBoolean(KEY_SEQUENTIAL_SINGLE_PAGE, true)
        val capName = prefs.getString(KEY_PREVIEW_CAP, PreviewResolutionCap.BALANCED.name)
        val cap = PreviewResolutionCap.entries.firstOrNull { it.name == capName }
            ?: PreviewResolutionCap.BALANCED
        return ProcessingPreferences(
            sequentialSinglePageProcessing = sequential,
            previewResolutionCap = cap,
        )
    }

    companion object {
        private const val PREFS_NAME = "bscan_preferences"
        private const val KEY_SEQUENTIAL_SINGLE_PAGE = "sequential_single_page"
        private const val KEY_PREVIEW_CAP = "preview_resolution_cap"
    }
}
