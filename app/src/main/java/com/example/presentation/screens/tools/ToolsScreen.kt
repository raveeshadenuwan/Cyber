package com.example.presentation.screens.tools

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.presentation.components.CyberGuardTopBar
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

@Composable
fun ToolsScreen(
    onNavigateToPassword: () -> Unit,
    onNavigateToUrlChecker: () -> Unit,
    onNavigateToRecommendations: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "SECURITY UTILITIES",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("tools_screen")
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "DEFENSIVE TOOLS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                ToolItemCard(
                    title = "Password Security Tool",
                    description = "Offline strength meter, entropy estimation & cryptographically secure random password generator.",
                    icon = Icons.Default.Lock,
                    onClick = onNavigateToPassword,
                    testTag = "tool_card_password"
                )
            }

            item {
                ToolItemCard(
                    title = "URL Security Analyzer",
                    description = "Heuristic safety inspector checking for typosquatting, IDN homographs, raw IP hosts, and phishing patterns.",
                    icon = Icons.Default.Public,
                    onClick = onNavigateToUrlChecker,
                    testTag = "tool_card_url"
                )
            }

            item {
                ToolItemCard(
                    title = "Hardening Recommendations",
                    description = "Curated actionable security checklist to reduce mobile attack surface and secure configurations.",
                    icon = Icons.Default.Lightbulb,
                    onClick = onNavigateToRecommendations,
                    testTag = "tool_card_recommendations"
                )
            }

            item {
                Text(
                    text = "RECORDS & DATA",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                ToolItemCard(
                    title = "Audit History",
                    description = "View previous security scans, historical scores, and resolved findings stored safely in local database.",
                    icon = Icons.Default.History,
                    onClick = onNavigateToHistory,
                    testTag = "tool_card_history"
                )
            }

            item {
                ToolItemCard(
                    title = "Privacy & Local Guarantees",
                    description = "Review our strict offline architecture: zero telemetry, zero credential logging, zero cloud transmission.",
                    icon = Icons.Default.PrivacyTip,
                    onClick = onNavigateToPrivacy,
                    testTag = "tool_card_privacy"
                )
            }
        }
    }
}

@Composable
private fun ToolItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberPrimary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
