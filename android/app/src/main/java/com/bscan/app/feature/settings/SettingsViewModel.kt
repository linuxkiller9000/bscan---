package com.bscan.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bscan.app.core.data.preferences.PreferencesStore
import com.bscan.app.core.data.repository.ProjectRepository
import com.bscan.app.core.data.storage.SampledBitmapLoader
import com.bscan.app.core.model.OcrLanguage
import com.bscan.app.core.model.PreviewResolutionCap
import com.bscan.app.core.model.ProcessingPreferences
import com.bscan.app.core.model.StorageUsageSummary
import com.bscan.app.core.model.SupportedOcrLanguages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val ocrLanguages: List<OcrLanguage> = SupportedOcrLanguages.initialCatalog,
    val processingPreferences: ProcessingPreferences = ProcessingPreferences(),
    val storageUsage: StorageUsageSummary = StorageUsageSummary(),
    val isRefreshingStorage: Boolean = false,
    val statusMessage: String? = null,
)

class SettingsViewModel(
    private val projectRepository: ProjectRepository,
    private val preferencesStore: PreferencesStore,
    private val bitmapLoader: SampledBitmapLoader,
) : ViewModel() {

    private val storageUsage = MutableStateFlow(StorageUsageSummary())
    private val isRefreshingStorage = MutableStateFlow(false)
    private val statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesStore.preferences,
        storageUsage,
        isRefreshingStorage,
        statusMessage,
    ) { prefs, storage, refreshing, message ->
        SettingsUiState(
            ocrLanguages = SupportedOcrLanguages.initialCatalog,
            processingPreferences = prefs,
            storageUsage = storage,
            isRefreshingStorage = refreshing,
            statusMessage = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    init {
        refreshStorageUsage()
    }

    fun refreshStorageUsage() {
        viewModelScope.launch {
            isRefreshingStorage.value = true
            storageUsage.value = runCatching {
                projectRepository.getStorageUsage()
            }.getOrDefault(StorageUsageSummary())
            isRefreshingStorage.value = false
        }
    }

    fun setSequentialSinglePageProcessing(enabled: Boolean) {
        preferencesStore.setSequentialSinglePageProcessing(enabled)
    }

    fun setPreviewResolutionCap(cap: PreviewResolutionCap) {
        preferencesStore.setPreviewResolutionCap(cap)
    }

    fun clearPreviewCache(confirmationText: String) {
        viewModelScope.launch {
            bitmapLoader.clearMemoryCache()
            projectRepository.clearPreviewCache()
            refreshStorageUsage()
            statusMessage.value = confirmationText
        }
    }

    fun clearStatusMessage() {
        statusMessage.value = null
    }

    companion object {
        fun factory(
            repository: ProjectRepository,
            preferencesStore: PreferencesStore,
            bitmapLoader: SampledBitmapLoader,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository, preferencesStore, bitmapLoader) as T
            }
        }
    }
}
