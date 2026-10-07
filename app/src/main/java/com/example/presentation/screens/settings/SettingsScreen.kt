package com.example.presentation.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.ThemeMode
import com.example.presentation.components.CyberGuardTopBar
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "PREFERENCES",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                showSettingsAction = false
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("settings_screen")
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme selection
            item {
                Text(
                    text = "APPEARANCE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ThemeOptionRow(
                            label = "Cyber Dark (Recommended)",
                            selected = prefs.themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                        )
                        ThemeOptionRow(
                            label = "Clean Light",
                            selected = prefs.themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                        )
                        ThemeOptionRow(
                            label = "Follow System Default",
                            selected = prefs.themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                        )
                    }
                }
            }

            // Notifications
            item {
                Text(
                    text = "NOTIFICATIONS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Security Reminders",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Periodic alerts to perform system audits",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = prefs.securityRemindersEnabled,
                            onCheckedChange = { viewModel.setSecurityReminders(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = CyberPrimary
                            )
                        )
                    }
                }
            }

            // Scan Preferences
            item {
                Text(
                    text = "SCAN PREFERENCES",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ToggleOptionRow(
                            label = "Include Application Permission Audit",
                            detail = "Analyze installed app packages for high-risk combinations",
                            checked = prefs.scanAppsEnabled,
                            onCheckedChange = { viewModel.setScanApps(it) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ToggleOptionRow(
                            label = "Include Network Interface Probing",
                            detail = "Evaluate VPN, metered routes, and connection posture",
                            checked = prefs.scanNetworkEnabled,
                            onCheckedChange = { viewModel.setScanNetwork(it) }
                        )
                    }
                }
            }

            // Information Links
            item {
                Text(
                    text = "APPLICATION & COMPLIANCE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        NavigationLinkRow(
                            title = "Privacy Policy & Offline Guarantee",
                            icon = Icons.Default.PrivacyTip,
                            onClick = onNavigateToPrivacy
                        )
                        NavigationLinkRow(
                            title = "About CyberGuard Mobile",
                            icon = Icons.Default.Info,
                            onClick = onNavigateToAbout
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = CyberPrimary)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ToggleOptionRow(label: String, detail: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = CyberPrimary
            )
        )
    }
}

@Composable
private fun NavigationLinkRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
