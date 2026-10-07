package com.example.presentation.screens.apps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.PermissionDetail
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.EmptyStateView
import com.example.presentation.components.ErrorStateView
import com.example.presentation.components.LoadingView
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed

@Composable
fun AppsScreen(
    viewModel: AppsViewModel,
    onNavigateToAppDetails: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    SensitivePermissionAppsScreen(
        viewModel = viewModel,
        onNavigateToAppDetails = onNavigateToAppDetails,
        onNavigateToSettings = onNavigateToSettings
    )
}

/**
 * Composable screen that lists installed applications with sensitive permissions,
 * categorizing them into risk levels (High, Medium, Low) using the permission analysis model.
 */
@Composable
fun SensitivePermissionAppsScreen(
    viewModel: AppsViewModel,
    onNavigateToAppDetails: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showExplanationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "PERMISSION AUDIT",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingView(
                message = "Scanning installed apps & evaluating sensitive permission models...",
                modifier = Modifier.padding(innerPadding)
            )
        } else if (state.errorMessage != null) {
            ErrorStateView(
                errorMessage = state.errorMessage ?: "Failed to inspect apps",
                onRetry = { viewModel.loadApps() },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .testTag("sensitive_permission_apps_screen")
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Risk Metric Cards
                item {
                    RiskOverviewSummary(
                        highCount = state.highRiskCount,
                        mediumCount = state.mediumRiskCount,
                        lowCount = state.lowRiskCount,
                        onRiskSelected = { risk -> viewModel.onFilterRiskSelected(risk) },
                        selectedRisk = state.selectedRiskFilter
                    )
                }

                // Permission Model Explanation Card
                item {
                    PermissionModelCard(
                        isExpanded = showExplanationDialog,
                        onToggleExpand = { showExplanationDialog = !showExplanationDialog }
                    )
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search by name or package...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        modifier = Modifier
                            .testTag("apps_search_input")
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberCardBorder
                        )
                    )
                }

                // Permission Category Filter Chips
                item {
                    Column {
                        Text(
                            text = "FILTER BY SENSITIVE PERMISSION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(PermissionFilterCategory.values()) { category ->
                                FilterChip(
                                    selected = state.selectedPermissionFilter == category,
                                    onClick = { viewModel.onPermissionCategorySelected(category) },
                                    label = { Text(category.displayName) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = getCategoryIcon(category),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberPrimary.copy(alpha = 0.2f),
                                        selectedLabelColor = CyberPrimary,
                                        selectedLeadingIconColor = CyberPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // Sorting and Results Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "APPLICATIONS (${state.filteredApps.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Sort toggle button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val nextSort = when (state.selectedSortOrder) {
                                AppSortOrder.BY_RISK_DESC -> AppSortOrder.BY_SENSITIVE_COUNT_DESC
                                AppSortOrder.BY_SENSITIVE_COUNT_DESC -> AppSortOrder.BY_NAME_ASC
                                AppSortOrder.BY_NAME_ASC -> AppSortOrder.BY_RISK_DESC
                            }
                            Text(
                                text = state.selectedSortOrder.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable { viewModel.onSortOrderSelected(nextSort) }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                // App Items List
                if (state.filteredApps.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "No Applications Match Filter",
                            message = "No applications match your active search or permission filters."
                        )
                    }
                } else {
                    items(state.filteredApps, key = { it.packageName }) { app ->
                        SensitiveAppCard(
                            app = app,
                            onCardClick = { onNavigateToAppDetails(app.packageName) },
                            onOpenSettings = { openAppSettings(context, app.packageName) }
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

@Composable
private fun RiskOverviewSummary(
    highCount: Int,
    mediumCount: Int,
    lowCount: Int,
    onRiskSelected: (AppRiskLevel?) -> Unit,
    selectedRisk: AppRiskLevel?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RiskMetricChip(
            title = "High Risk",
            count = highCount,
            color = SecurityRed,
            isSelected = selectedRisk == AppRiskLevel.HIGH,
            onClick = {
                onRiskSelected(if (selectedRisk == AppRiskLevel.HIGH) null else AppRiskLevel.HIGH)
            },
            modifier = Modifier.weight(1f)
        )
        RiskMetricChip(
            title = "Medium Risk",
            count = mediumCount,
            color = SecurityAmber,
            isSelected = selectedRisk == AppRiskLevel.MEDIUM,
            onClick = {
                onRiskSelected(if (selectedRisk == AppRiskLevel.MEDIUM) null else AppRiskLevel.MEDIUM)
            },
            modifier = Modifier.weight(1f)
        )
        RiskMetricChip(
            title = "Low Risk",
            count = lowCount,
            color = SecurityGreen,
            isSelected = selectedRisk == AppRiskLevel.LOW,
            onClick = {
                onRiskSelected(if (selectedRisk == AppRiskLevel.LOW) null else AppRiskLevel.LOW)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RiskMetricChip(
    title: String,
    count: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else CyberCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PermissionModelCard(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Permission Risk Model Guidelines",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Apps are categorized strictly by sensitive permission attack surface combinations:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ModelBullet(
                        bulletColor = SecurityRed,
                        title = "High Risk",
                        description = "User apps holding cross-domain sensitive access (e.g., SMS/Contacts combined with Location or Camera/Audio), or granting ≥4 sensitive permissions."
                    )
                    ModelBullet(
                        bulletColor = SecurityAmber,
                        title = "Medium Risk",
                        description = "Apps possessing standalone sensitive permissions such as GPS Location, Camera, Microphone, or Phone State."
                    )
                    ModelBullet(
                        bulletColor = SecurityGreen,
                        title = "Low Risk",
                        description = "Applications requesting minimal or standard non-intrusive permissions."
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Note: A high-risk categorization identifies attack surface exposure and does NOT automatically indicate malicious activity.",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelBullet(bulletColor: Color, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
                .background(bulletColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = bulletColor
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SensitiveAppCard(
    app: InstalledAppInfo,
    onCardClick: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val (riskColor, riskBg) = when (app.riskLevel) {
        AppRiskLevel.HIGH -> Pair(SecurityRed, SecurityRed.copy(alpha = 0.15f))
        AppRiskLevel.MEDIUM -> Pair(SecurityAmber, SecurityAmber.copy(alpha = 0.15f))
        AppRiskLevel.LOW -> Pair(SecurityGreen, SecurityGreen.copy(alpha = 0.15f))
    }

    val grantedSensitives = app.requestedPermissions.filter { it.isSensitive && it.isGranted }

    Card(
        modifier = Modifier
            .testTag("app_card_${app.packageName}")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Names, Risk Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CyberPrimary.copy(alpha = 0.12f))
                        .border(1.dp, riskColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyberPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (app.isSystemApp) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberSurfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "System",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(riskBg)
                        .border(1.dp, riskColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = app.riskLevel.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = riskColor
                    )
                }
            }

            // Sensitive Permissions Chips
            if (grantedSensitives.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    grantedSensitives.forEach { perm ->
                        PermissionChipBadge(detail = perm)
                    }
                }
            }

            // Risk Factors Summary
            if (app.riskReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceVariant.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    Column {
                        app.riskReasons.forEach { reason ->
                            Text(
                                text = "• $reason",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Action Row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${grantedSensitives.size} active sensitive perm(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row {
                    TextButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = CyberPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Settings", style = MaterialTheme.typography.labelSmall, color = CyberPrimary)
                    }

                    TextButton(onClick = onCardClick) {
                        Text("Inspect", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionChipBadge(detail: PermissionDetail) {
    val icon = when {
        detail.permission.contains("CAMERA", ignoreCase = true) -> Icons.Default.CameraAlt
        detail.permission.contains("RECORD_AUDIO", ignoreCase = true) -> Icons.Default.Mic
        detail.permission.contains("LOCATION", ignoreCase = true) -> Icons.Default.MyLocation
        detail.permission.contains("SMS", ignoreCase = true) -> Icons.Default.ContactPage
        detail.permission.contains("CONTACTS", ignoreCase = true) -> Icons.Default.ContactPage
        detail.permission.contains("STORAGE", ignoreCase = true) || detail.permission.contains("MEDIA", ignoreCase = true) -> Icons.Default.Folder
        else -> Icons.Default.Phone
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .border(0.5.dp, CyberCardBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyberPrimary,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = detail.simpleName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getCategoryIcon(category: PermissionFilterCategory): ImageVector {
    return when (category) {
        PermissionFilterCategory.ALL -> Icons.Default.Shield
        PermissionFilterCategory.LOCATION -> Icons.Default.MyLocation
        PermissionFilterCategory.CAMERA -> Icons.Default.CameraAlt
        PermissionFilterCategory.MICROPHONE -> Icons.Default.Mic
        PermissionFilterCategory.SMS_CONTACTS -> Icons.Default.ContactPage
        PermissionFilterCategory.STORAGE -> Icons.Default.Folder
        PermissionFilterCategory.PHONE -> Icons.Default.Phone
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
