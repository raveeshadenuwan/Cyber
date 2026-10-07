package com.example.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Scan : Screen("scan")
    object Apps : Screen("apps")
    object AppDetails : Screen("app_details/{packageName}") {
        fun createRoute(packageName: String) = "app_details/$packageName"
    }
    object Network : Screen("network")
    object Tools : Screen("tools")
    object Password : Screen("password")
    object UrlChecker : Screen("url_checker")
    object Recommendations : Screen("recommendations")
    object ScanHistory : Screen("scan_history")
    object Settings : Screen("settings")
    object About : Screen("about")
    object Privacy : Screen("privacy")
}
