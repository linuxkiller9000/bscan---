package com.bscan.app.feature.importmedia

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bscan.app.core.data.preferences.PreferencesStore
import com.bscan.app.core.data.repository.ProjectRepository
import com.bscan.app.core.model.PreviewResolutionCap
import com.bscan.app.core.model.ScanProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ImportedPreviewUiState(
    val project: ScanProject? = null,
    val selectedPageIndex: Int = 0,
    val previewMaxEdgePx: Int = PreviewResolutionCap.BALANCED.maxLongEdgePx,
    val isLoading: Boolean = true,
    val isAddingPages: Boolean = false,
    val errorMessage: String? = null,
)

class ImportedPreviewViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    preferencesStore: PreferencesStore,
) : ViewModel() {

    private val selectedPageIndex = MutableStateFlow(0)
    private val isAddingPages = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ImportedPreviewUiState> = combine(
        projectRepository.observeProject(projectId),
        selectedPageIndex,
        preferencesStore.preferences,
        isAddingPages,
        errorMessage,
    ) { project, pageIdx, prefs, adding, error ->
        val safeIndex = if (project == null || project.pages.isEmpty()) {
            0
        } else {
            pageIdx.coerceIn(0, project.pages.lastIndex)
        }
        ImportedPreviewUiState(
            project = project,
            selectedPageIndex = safeIndex,
            previewMaxEdgePx = prefs.previewResolutionCap.maxLongEdgePx,
            isLoading = false,
            isAddingPages = adding,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ImportedPreviewUiState(isLoading = true),
    )

    fun selectPage(index: Int) {
        selectedPageIndex.value = index.coerceAtLeast(0)
    }

    fun addPages(uris: List<Uri>, fallbackErrorMessage: String) {
        if (uris.isEmpty() || isAddingPages.value) return
        viewModelScope.launch {
            isAddingPages.value = true
            errorMessage.value = null
            val previousCount = uiState.value.project?.pages?.size ?: 0
            val result = projectRepository.addImagesToProject(projectId, uris)
            isAddingPages.value = false
            result.fold(
                onSuccess = { addedCount ->
                    if (addedCount > 0) {
                        selectedPageIndex.value = previousCount
                    }
                },
                onFailure = { err ->
                    errorMessage.value = err.localizedMessage?.takeIf { it.isNotBlank() }
                        ?: fallbackErrorMessage
                },
            )
        }
    }

    fun dismissError() {
        errorMessage.value = null
    }

    companion object {
        fun factory(
            projectId: String,
            repository: ProjectRepository,
            preferencesStore: PreferencesStore,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ImportedPreviewViewModel(projectId, repository, preferencesStore) as T
            }
        }
    }
}
