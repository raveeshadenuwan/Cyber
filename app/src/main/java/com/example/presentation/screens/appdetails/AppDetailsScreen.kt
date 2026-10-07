package com.example.presentation.screens.appdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.PermissionDetail
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.EmptyStateView
import com.example.presentation.components.PrimaryCyberButton
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
fun AppDetailsScreen(
    app: InstalledAppInfo?,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "PACKAGE DETAILS",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        if (app == null) {
            EmptyStateView(
                title = "Application Not Found",
                message = "Details for this package are not accessible.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .testTag("app_details_screen")
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card
                item {
                    AppHeaderCard(app = app)
                }

                // Risk Analysis Card
                item {
                    AppRiskCard(app = app)
                }

                // Open in System Settings
                item {
                    PrimaryCyberButton(
                        text = "Manage in Android Settings",
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        onClick = { openAppSettings(context, app.packageName) },
                        testTag = "open_app_settings_button"
                    )
                }

                // Permissions List Header
                item {
                    Text(
                        text = "DECLARED PERMISSIONS (${app.requestedPermissions.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(app.requestedPermissions, key = { it.permission }) { perm ->
                    PermissionDetailRow(detail = perm)
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun AppHeaderCard(app: InstalledAppInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberPrimary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Version: ${app.versionName} · Target API: ${app.targetSdk}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (app.isSystemApp) "System Application" else "User Installed Application",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (app.installTime > 0) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Text(
                        text = "Installed: ${sdf.format(Date(app.installTime))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRiskCard(app: InstalledAppInfo) {
    val (color, label) = when (app.riskLevel) {
        AppRiskLevel.HIGH -> Pair(SecurityRed, "High Permission Footprint")
        AppRiskLevel.MEDIUM -> Pair(SecurityAmber, "Moderate Permission Footprint")
        AppRiskLevel.LOW -> Pair(SecurityGreen, "Standard / Low Permission Footprint")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEURISTIC RISK ASSESSMENT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
                Text(
                    text = app.riskLevel.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (app.riskReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                app.riskReasons.forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Note: Sensitive permissions are evaluated for attack surface analysis. High risk classification indicates permission breadth, not malware presence.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PermissionDetailRow(detail: PermissionDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.simpleName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detail.permission,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = detail.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Granted / Not Granted Badge
            if (detail.isGranted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecurityGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Granted",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SecurityGreen
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Gray.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Requested",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

private fun openAppSettings(context: Context, packageName: String) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}
