package com.example.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.components.CyberGuardBottomBar
import com.example.presentation.screens.about.AboutScreen
import com.example.presentation.screens.appdetails.AppDetailsScreen
import com.example.presentation.screens.apps.AppsScreen
import com.example.presentation.screens.apps.AppsViewModel
import com.example.presentation.screens.dashboard.DashboardScreen
import com.example.presentation.screens.dashboard.DashboardViewModel
import com.example.presentation.screens.history.ScanHistoryScreen
import com.example.presentation.screens.history.ScanHistoryViewModel
import com.example.presentation.screens.network.NetworkScreen
import com.example.presentation.screens.network.NetworkViewModel
import com.example.presentation.screens.password.PasswordToolsScreen
import com.example.presentation.screens.password.PasswordViewModel
import com.example.presentation.screens.privacy.PrivacyScreen
import com.example.presentation.screens.recommendations.RecommendationsScreen
import com.example.presentation.screens.scan.ScanScreen
import com.example.presentation.screens.scan.ScanViewModel
import com.example.presentation.screens.settings.SettingsScreen
import com.example.presentation.screens.settings.SettingsViewModel
import com.example.presentation.screens.splash.SplashScreen
import com.example.presentation.screens.tools.ToolsScreen
import com.example.presentation.screens.urlchecker.UrlCheckerScreen
import com.example.presentation.screens.urlchecker.UrlCheckerViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = setOf(
        Screen.Dashboard.route,
        Screen.Scan.route,
        Screen.Apps.route,
        Screen.Network.route,
        Screen.Tools.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    // Shared ViewModels where needed
    val appsViewModel: AppsViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                CyberGuardBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Dashboard
            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = viewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToApps = { navController.navigate(Screen.Apps.route) },
                    onNavigateToNetwork = { navController.navigate(Screen.Network.route) },
                    onNavigateToPassword = { navController.navigate(Screen.Password.route) },
                    onNavigateToUrlChecker = { navController.navigate(Screen.UrlChecker.route) },
                    onNavigateToRecommendations = { navController.navigate(Screen.Recommendations.route) },
                    onNavigateToHistory = { navController.navigate(Screen.ScanHistory.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Scan
            composable(Screen.Scan.route) {
                val scanViewModel: ScanViewModel = viewModel()
                ScanScreen(
                    viewModel = scanViewModel,
                    onNavigateToRecommendations = { navController.navigate(Screen.Recommendations.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Apps
            composable(Screen.Apps.route) {
                AppsScreen(
                    viewModel = appsViewModel,
                    onNavigateToAppDetails = { pkgName ->
                        navController.navigate(Screen.AppDetails.createRoute(pkgName))
                    },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // App Details
            composable(
                route = Screen.AppDetails.route,
                arguments = listOf(navArgument("packageName") { type = NavType.StringType })
            ) { backStackEntry ->
                val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                val app = appsViewModel.getAppByPackageName(packageName)
                AppDetailsScreen(
                    app = app,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Network
            composable(Screen.Network.route) {
                val networkViewModel: NetworkViewModel = viewModel()
                NetworkScreen(
                    viewModel = networkViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Tools Hub
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateToPassword = { navController.navigate(Screen.Password.route) },
                    onNavigateToUrlChecker = { navController.navigate(Screen.UrlChecker.route) },
                    onNavigateToRecommendations = { navController.navigate(Screen.Recommendations.route) },
                    onNavigateToHistory = { navController.navigate(Screen.ScanHistory.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Password Tool
            composable(Screen.Password.route) {
                val passwordViewModel: PasswordViewModel = viewModel()
                PasswordToolsScreen(
                    viewModel = passwordViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // URL Checker
            composable(Screen.UrlChecker.route) {
                val urlCheckerViewModel: UrlCheckerViewModel = viewModel()
                UrlCheckerScreen(
                    viewModel = urlCheckerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Recommendations
            composable(Screen.Recommendations.route) {
                RecommendationsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // History
            composable(Screen.ScanHistory.route) {
                val historyViewModel: ScanHistoryViewModel = viewModel()
                ScanHistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }

            // About
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Privacy
            composable(Screen.Privacy.route) {
                PrivacyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
