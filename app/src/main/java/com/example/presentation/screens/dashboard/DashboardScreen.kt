package com.example.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecuritySeverity
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.ErrorStateView
import com.example.presentation.components.LoadingView
import com.example.presentation.components.PrimaryCyberButton
import com.example.presentation.components.SecurityScoreCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToUrlChecker: () -> Unit,
    onNavigateToRecommendations: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val latestScanEntity by viewModel.latestScanFlow.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "CYBERGUARD HUB",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingView(
                message = "Aggregating device, permission, and network telemetry...",
                modifier = Modifier.padding(innerPadding)
            )
        } else if (state.errorMessage != null) {
            ErrorStateView(
                errorMessage = state.errorMessage ?: "Unknown error",
                onRetry = { viewModel.loadDashboardData() },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .testTag("dashboard_screen")
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Central Posture Score Gauge
                item {
                    SecurityScoreCard(
                        score = state.score,
                        lastScanTime = latestScanEntity?.timestamp ?: state.lastScanTimestamp
                    )
                }

                // Primary Audit Trigger Button
                item {
                    PrimaryCyberButton(
                        text = "EXECUTE DEEP SECURITY AUDIT",
                        icon = Icons.Default.Radar,
                        onClick = onNavigateToScan,
                        testTag = "dashboard_start_scan_button"
                    )
                }

                // Central Telemetry Hub Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY TELEMETRY PILLARS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { viewModel.loadDashboardData() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh telemetry",
                                tint = CyberPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Hub Pillar 1: Device Security Scanner Summary
                item {
                    DeviceScannerHubCard(
                        checks = state.checks,
                        onNavigateToScan = onNavigateToScan,
                        onOpenSettings = { action -> viewModel.openSystemSettings(action) }
                    )
                }

                // Hub Pillar 2: Permission Monitor Summary
                item {
                    PermissionMonitorHubCard(
                        totalApps = state.totalAppsCount,
                        highRiskCount = state.highRiskAppsCount,
                        mediumRiskCount = state.mediumRiskAppsCount,
                        lowRiskCount = state.lowRiskAppsCount,
                        sensitiveAppsCount = state.sensitiveAppsCount,
                        topRiskApps = state.topRiskApps,
                        onNavigateToApps = onNavigateToApps
                    )
                }

                // Hub Pillar 3: Network Analyzer Summary
                item {
                    state.networkInfo?.let { networkInfo ->
                        NetworkAnalyzerHubCard(
                            networkInfo = networkInfo,
                            onNavigateToNetwork = onNavigateToNetwork
                        )
                    }
                }

                // Quick Security Tools Grid
                item {
                    Text(
                        text = "DEFENSIVE TOOLS & LOGS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickToolCard(
                                title = "Password Tool",
                                subtitle = "Offline generator & analyzer",
                                icon = Icons.Default.Lock,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToPassword,
                                testTag = "quick_action_password"
                            )
                            QuickToolCard(
                                title = "URL Checker",
                                subtitle = "Heuristic safety inspector",
                                icon = Icons.Default.Public,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToUrlChecker,
                                testTag = "quick_action_url"
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickToolCard(
                                title = "Hardening Guide",
                                subtitle = "Curated remediation rules",
                                icon = Icons.Default.Lightbulb,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToRecommendations,
                                testTag = "quick_action_recommendations"
                            )
                            QuickToolCard(
                                title = "Audit Logs",
                                subtitle = "Historical posture records",
                                icon = Icons.Default.History,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToHistory,
                                testTag = "quick_action_history"
                            )
                        }
                    }
                }

                // Recent Audit Activity Feed (from Room)
                if (latestScanEntity != null) {
                    item {
                        RecentAuditCard(
                            lastScanTime = latestScanEntity!!.timestamp,
                            score = latestScanEntity!!.score,
                            criticalCount = latestScanEntity!!.criticalCount,
                            warningCount = latestScanEntity!!.warningCount,
                            onViewHistory = onNavigateToHistory
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

/**
 * Hub Card 1: Device Security Scanner Summary
 */
@Composable
private fun DeviceScannerHubCard(
    checks: List<SecurityCheck>,
    onNavigateToScan: () -> Unit,
    onOpenSettings: (String) -> Unit
) {
    val lockCheck = checks.firstOrNull { it.id == "screen_lock" }
    val usbCheck = checks.firstOrNull { it.id == "usb_debugging" }
    val devCheck = checks.firstOrNull { it.id == "dev_options" }
    val patchCheck = checks.firstOrNull { it.id == "security_patch" }

    val criticalIssues = checks.filter { it.severity == SecuritySeverity.CRITICAL }

    Card(
        modifier = Modifier
            .testTag("hub_card_device_scanner")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .clickable { onNavigateToScan() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DEVICE SCANNER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyberPrimary
                        )
                        Text(
                            text = "Hardware & OS Integrity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Status Key-Value Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HubMetricItem(
                    label = "Screen Lock",
                    statusText = if (lockCheck?.severity == SecuritySeverity.SECURE) "Secure" else "Missing",
                    statusColor = if (lockCheck?.severity == SecuritySeverity.SECURE) SecurityGreen else SecurityRed
                )
                HubMetricItem(
                    label = "USB Debugging",
                    statusText = if (usbCheck?.severity == SecuritySeverity.SECURE) "Disabled" else "Active",
                    statusColor = if (usbCheck?.severity == SecuritySeverity.SECURE) SecurityGreen else SecurityAmber
                )
                HubMetricItem(
                    label = "Developer Mode",
                    statusText = if (devCheck?.severity == SecuritySeverity.SECURE) "Off" else "On",
                    statusColor = if (devCheck?.severity == SecuritySeverity.SECURE) SecurityGreen else SecurityAmber
                )
                HubMetricItem(
                    label = "Patch Level",
                    statusText = if (patchCheck?.severity == SecuritySeverity.SECURE) "Current" else "Inspect",
                    statusColor = if (patchCheck?.severity == SecuritySeverity.SECURE) SecurityGreen else SecurityAmber
                )
            }

            // Actionable Resolution Prompt if Critical Findings exist
            if (criticalIssues.isNotEmpty()) {
                val issue = criticalIssues.first()
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecurityRed.copy(alpha = 0.12f))
                        .border(1.dp, SecurityRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SecurityRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${issue.title} requires attention",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecurityRed,
                            maxLines = 1
                        )
                    }
                    if (issue.settingsAction != null) {
                        TextButton(
                            onClick = { onOpenSettings(issue.settingsAction) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Resolve", style = MaterialTheme.typography.labelSmall, color = CyberPrimary)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Hub Card 2: Permission Monitor Summary
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PermissionMonitorHubCard(
    totalApps: Int,
    highRiskCount: Int,
    mediumRiskCount: Int,
    lowRiskCount: Int,
    sensitiveAppsCount: Int,
    topRiskApps: List<InstalledAppInfo>,
    onNavigateToApps: () -> Unit
) {
    Card(
        modifier = Modifier
            .testTag("hub_card_permission_monitor")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .clickable { onNavigateToApps() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PERMISSION MONITOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyberPrimary
                        )
                        Text(
                            text = "$totalApps Installed Applications",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Risk Breakdown Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiskPill(label = "High Risk", count = highRiskCount, color = SecurityRed, modifier = Modifier.weight(1f))
                RiskPill(label = "Medium Risk", count = mediumRiskCount, color = SecurityAmber, modifier = Modifier.weight(1f))
                RiskPill(label = "Low Risk", count = lowRiskCount, color = SecurityGreen, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "$sensitiveAppsCount applications possess sensitive permissions (Location, Mic, Camera, SMS)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Top Risk Apps Preview
            if (topRiskApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    topRiskApps.forEach { app ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SecurityRed.copy(alpha = 0.12f))
                                .border(0.5.dp, SecurityRed.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "⚠️ ${app.appName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = SecurityRed
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Hub Card 3: Network Analyzer Summary
 */
@Composable
private fun NetworkAnalyzerHubCard(
    networkInfo: NetworkSecurityInfo,
    onNavigateToNetwork: () -> Unit
) {
    val statusColor = if (networkInfo.isConnected) SecurityGreen else SecurityRed
    val icon = if (networkInfo.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff

    Card(
        modifier = Modifier
            .testTag("hub_card_network_analyzer")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .clickable { onNavigateToNetwork() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NETWORK ANALYZER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                        Text(
                            text = "${networkInfo.networkType.displayName} ${if (networkInfo.isConnected) "Online" else "Offline"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Network telemetry grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HubMetricItem(
                    label = "Local IPv4",
                    statusText = networkInfo.localIpAddress.take(15),
                    statusColor = MaterialTheme.colorScheme.onSurface
                )
                HubMetricItem(
                    label = "VPN Encapsulation",
                    statusText = if (networkInfo.isVpnActive) "Active" else "None",
                    statusColor = if (networkInfo.isVpnActive) CyberPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                HubMetricItem(
                    label = "Data Route",
                    statusText = if (networkInfo.isMetered) "Metered" else "Unmetered",
                    statusColor = if (networkInfo.isMetered) SecurityAmber else SecurityGreen
                )
            }
        }
    }
}

@Composable
private fun HubMetricItem(
    label: String,
    statusText: String,
    statusColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = statusColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RiskPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .height(96.dp)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CyberPrimary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RecentAuditCard(
    lastScanTime: Long,
    score: Int,
    criticalCount: Int,
    warningCount: Int,
    onViewHistory: () -> Unit
) {
    val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
    val formatted = sdf.format(Date(lastScanTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onViewHistory() },
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
                    text = "LAST COMPLETED AUDIT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$formatted — Score: $score/100",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$criticalCount critical · $warningCount warnings recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View scan history",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
