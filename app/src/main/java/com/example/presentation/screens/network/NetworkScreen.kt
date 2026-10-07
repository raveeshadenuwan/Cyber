package com.example.presentation.screens.network

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.NetworkType
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.ErrorStateView
import com.example.presentation.components.LoadingView
import com.example.presentation.components.PrimaryCyberButton
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed

@Composable
fun NetworkScreen(
    viewModel: NetworkViewModel,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "NETWORK POSTURE",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingView(
                message = "Probing network parameters & interfaces...",
                modifier = Modifier.padding(innerPadding)
            )
        } else if (state.errorMessage != null) {
            ErrorStateView(
                errorMessage = state.errorMessage ?: "Network query failure",
                onRetry = { viewModel.refreshNetwork() },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (state.networkInfo != null) {
            val info = state.networkInfo!!

            LazyColumn(
                modifier = Modifier
                    .testTag("network_screen")
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Connection Status Overview Card
                item {
                    ConnectionOverviewCard(info = info)
                }

                // Refresh Button
                item {
                    PrimaryCyberButton(
                        text = "Refresh Network",
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.refreshNetwork() },
                        testTag = "refresh_network_button"
                    )
                }

                // Interface & IP details
                item {
                    Text(
                        text = "NETWORK INTERFACE DETAILS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    NetworkParamCard(
                        title = "Local IPv4 Address",
                        value = info.localIpAddress,
                        detail = "Device address on current local area network"
                    )
                }

                item {
                    NetworkParamCard(
                        title = "Default Gateway",
                        value = info.gateway ?: "Not detected",
                        detail = "Upstream router / hotspot hop"
                    )
                }

                item {
                    NetworkParamCard(
                        title = "Interface Name",
                        value = info.interfaceName ?: "Not bound",
                        detail = "Hardware network interface socket"
                    )
                }

                if (info.linkSpeedMbps != null) {
                    item {
                        NetworkParamCard(
                            title = "Link Speed",
                            value = "${info.linkSpeedMbps} Mbps",
                            detail = "Current physical link throughput"
                        )
                    }
                }

                if (info.dnsServers.isNotEmpty()) {
                    item {
                        NetworkParamCard(
                            title = "Configured DNS Servers",
                            value = info.dnsServers.joinToString(", "),
                            detail = "Domain name resolution endpoints"
                        )
                    }
                }

                // Android Privacy & Security Limitations Card
                if (info.limitations.isNotEmpty()) {
                    item {
                        Text(
                            text = "SECURITY RESTRICTIONS & OS LIMITATIONS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        LimitationsCard(limitations = info.limitations)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ConnectionOverviewCard(info: NetworkSecurityInfo) {
    val statusColor = if (info.isConnected) SecurityGreen else SecurityRed
    val icon = if (info.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = info.networkType.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (info.isConnected) "Online / Protected" else "No Internet Connectivity",
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (info.isConnected) "ACTIVE" else "OFFLINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Indicators Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusIndicator(
                    label = "VPN Tunnel",
                    value = if (info.isVpnActive) "Active" else "None",
                    color = if (info.isVpnActive) CyberPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusIndicator(
                    label = "Data Metering",
                    value = if (info.isMetered) "Metered" else "Unmetered",
                    color = if (info.isMetered) SecurityAmber else SecurityGreen
                )
                StatusIndicator(
                    label = "SSID",
                    value = info.wifiSsid ?: "Protected",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NetworkParamCard(
    title: String,
    value: String,
    detail: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LimitationsCard(limitations: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyberPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Android Privacy Protections",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            limitations.forEach { limit ->
                Text(
                    text = "• $limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
