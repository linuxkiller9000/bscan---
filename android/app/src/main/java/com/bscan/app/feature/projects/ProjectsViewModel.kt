package com.bscan.app.feature.projects

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bscan.app.core.data.repository.ProjectRepository
import com.bscan.app.core.model.ScanProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val projects: List<ScanProject> = emptyList(),
    val isImporting: Boolean = false,
    val errorMessage: String? = null,
)

class ProjectsViewModel(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    private val isImporting = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProjectsUiState> = combine(
        projectRepository.observeAllProjects(),
        isImporting,
        errorMessage,
    ) { projects, importing, error ->
        ProjectsUiState(
            projects = projects,
            isImporting = importing,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProjectsUiState(),
    )

    fun importImages(
        uris: List<Uri>,
        fallbackErrorMessage: String,
        onProjectCreated: (String) -> Unit,
    ) {
        if (uris.isEmpty() || isImporting.value) return
        viewModelScope.launch {
            isImporting.value = true
            errorMessage.value = null
            val result = projectRepository.importImagesAsNewProject(uris)
            isImporting.value = false
            result.fold(
                onSuccess = { project -> onProjectCreated(project.id) },
                onFailure = { err ->
                    errorMessage.value = err.localizedMessage?.takeIf { it.isNotBlank() }
                        ?: fallbackErrorMessage
                },
            )
        }
    }

    fun reportError(message: String) {
        errorMessage.value = message
    }

    fun renameProject(projectId: String, newName: String) {
        viewModelScope.launch {
            projectRepository.renameProject(projectId, newName)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }

    fun dismissError() {
        errorMessage.value = null
    }

    companion object {
        fun factory(repository: ProjectRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProjectsViewModel(repository) as T
                }
            }
    }
}
