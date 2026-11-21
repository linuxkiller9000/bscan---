package com.bscan.app.navigation

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bscan.app.AppContainer
import com.bscan.app.R
import com.bscan.app.core.ui.icons.BscanIcons
import com.bscan.app.feature.camera.CameraFoundationScreen
import com.bscan.app.feature.home.HomeScreen
import com.bscan.app.feature.home.HomeViewModel
import com.bscan.app.feature.importmedia.ImportedPreviewScreen
import com.bscan.app.feature.importmedia.ImportedPreviewViewModel
import com.bscan.app.feature.importmedia.rememberGalleryImagePicker
import com.bscan.app.feature.projects.ProjectsScreen
import com.bscan.app.feature.projects.ProjectsViewModel
import com.bscan.app.feature.settings.SettingsScreen
import com.bscan.app.feature.settings.SettingsViewModel

object BscanRoutes {
    const val HOME = "home"
    const val PROJECTS = "projects"
    const val SETTINGS = "settings"
    const val CAMERA = "camera"
    const val PREVIEW = "preview/{projectId}"

    fun preview(projectId: String): String = "preview/${Uri.encode(projectId)}"
}

private enum class TopLevelTab(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
) {
    HOME(BscanRoutes.HOME, R.string.nav_home, Icons.Outlined.Home),
    PROJECTS(BscanRoutes.PROJECTS, R.string.nav_projects, BscanIcons.Folder),
    SETTINGS(BscanRoutes.SETTINGS, R.string.nav_settings, Icons.Outlined.Settings),
}

@Composable
fun BscanRootApp(
    container: AppContainer,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val topLevelRoutes = TopLevelTab.entries.map { it.route }.toSet()
    val showBottomBar = currentDestination?.route in topLevelRoutes

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    TopLevelTab.entries.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == tab.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = stringResource(tab.labelRes),
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(tab.labelRes),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BscanRoutes.HOME,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(BscanRoutes.HOME) {
                val vm: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(container.projectRepository),
                )
                HomeScreen(
                    viewModel = vm,
                    bitmapLoader = container.bitmapLoader,
                    onScanDocumentClick = { navController.navigate(BscanRoutes.CAMERA) },
                    onOpenProject = { projectId ->
                        navController.navigate(BscanRoutes.preview(projectId))
                    },
                    onViewAllProjects = {
                        navController.navigate(BscanRoutes.PROJECTS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            composable(BscanRoutes.PROJECTS) {
                val vm: ProjectsViewModel = viewModel(
                    factory = ProjectsViewModel.factory(container.projectRepository),
                )
                ProjectsScreen(
                    viewModel = vm,
                    bitmapLoader = container.bitmapLoader,
                    onOpenProject = { projectId ->
                        navController.navigate(BscanRoutes.preview(projectId))
                    },
                )
            }

            composable(BscanRoutes.SETTINGS) {
                val vm: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.factory(
                        repository = container.projectRepository,
                        preferencesStore = container.preferencesStore,
                        bitmapLoader = container.bitmapLoader,
                    ),
                )
                SettingsScreen(viewModel = vm)
            }

            composable(BscanRoutes.CAMERA) {
                val homeVm: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(container.projectRepository),
                )
                val fallbackErr = stringResource(R.string.preview_import_error)
                val launchGallery = rememberGalleryImagePicker(
                    onImagesPicked = { uris ->
                        homeVm.importImages(
                            uris = uris,
                            fallbackErrorMessage = fallbackErr,
                            onProjectCreated = { projectId ->
                                navController.navigate(BscanRoutes.preview(projectId)) {
                                    popUpTo(BscanRoutes.CAMERA) { inclusive = true }
                                }
                            },
                        )
                    },
                )
                CameraFoundationScreen(
                    onBack = { navController.popBackStack() },
                    onImportFromGallery = { launchGallery() },
                )
            }

            composable(
                route = BscanRoutes.PREVIEW,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
            ) { entry ->
                val projectId = entry.arguments?.getString("projectId").orEmpty()
                val vm: ImportedPreviewViewModel = viewModel(
                    factory = ImportedPreviewViewModel.factory(
                        projectId = projectId,
                        repository = container.projectRepository,
                        preferencesStore = container.preferencesStore,
                    ),
                )
                ImportedPreviewScreen(
                    viewModel = vm,
                    bitmapLoader = container.bitmapLoader,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
