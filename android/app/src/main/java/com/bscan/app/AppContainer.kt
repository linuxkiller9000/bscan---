package com.bscan.app

import android.content.Context
import com.bscan.app.core.data.db.BscanDatabase
import com.bscan.app.core.data.preferences.PreferencesStore
import com.bscan.app.core.data.repository.LocalProjectRepository
import com.bscan.app.core.data.repository.ProjectRepository
import com.bscan.app.core.data.storage.AppFileManager
import com.bscan.app.core.data.storage.SampledBitmapLoader

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: BscanDatabase by lazy { BscanDatabase.create(appContext) }
    val fileManager: AppFileManager by lazy { AppFileManager(appContext) }
    val bitmapLoader: SampledBitmapLoader by lazy { SampledBitmapLoader() }
    val preferencesStore: PreferencesStore by lazy { PreferencesStore(appContext) }

    val projectRepository: ProjectRepository by lazy {
        LocalProjectRepository(
            projectDao = database.projectDao(),
            fileManager = fileManager,
        )
    }
}
