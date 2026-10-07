package com.example.presentation.components

import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Dashboard : BottomNavDestination("dashboard", "Dashboard", Icons.Default.Dashboard, "tab_dashboard")
    object Scan : BottomNavDestination("scan", "Scan", Icons.Default.Radar, "tab_scan")
    object Apps : BottomNavDestination("apps", "Apps", Icons.Default.Apps, "tab_apps")
    object Network : BottomNavDestination("network", "Network", Icons.Default.Wifi, "tab_network")
    object Tools : BottomNavDestination("tools", "Tools", Icons.Default.Build, "tab_tools")
}

@Composable
fun CyberGuardBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavDestination.Dashboard,
        BottomNavDestination.Scan,
        BottomNavDestination.Apps,
        BottomNavDestination.Network,
        BottomNavDestination.Tools
    )

    NavigationBar(
        modifier = Modifier
            .testTag("cyberguard_bottom_bar")
            .border(width = 0.5.dp, color = CyberCardBorder),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                modifier = Modifier.testTag(item.testTag),
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.background,
                    selectedTextColor = CyberPrimary,
                    indicatorColor = CyberPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
