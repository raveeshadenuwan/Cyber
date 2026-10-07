package com.example.presentation.screens.privacy

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.presentation.components.CyberGuardTopBar
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.SecurityGreen

@Composable
fun PrivacyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "PRIVACY GUARANTEES",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                showSettingsAction = false
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("privacy_screen")
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = CyberPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "ZERO CLOUD TRANSMISSION",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "CyberGuard Mobile was designed under the principle that security utilities should never create privacy hazards. All computations happen directly inside your device's isolated process memory.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "SPECIFIC PLEDGES",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                PrivacyGuaranteeCard(
                    title = "Passwords are Never Stored or Transmitted",
                    description = "When you evaluate a passphrase in the strength meter or create one with the generator, the text exists solely in RAM. Passwords are never persisted to disk, never printed to logs, and never sent to any network socket."
                )
            }

            item {
                PrivacyGuaranteeCard(
                    title = "Scan Results are Stored Exclusively On-Device",
                    description = "Your device security findings, patch status, and posture logs are written into a private SQLite database via Room. No telemetry or audit results leave your hardware."
                )
            }

            item {
                PrivacyGuaranteeCard(
                    title = "No Background Surveillance or Device Exploitation",
                    description = "CyberGuard does not run persistent background listeners or monitor keystrokes. It inspects public Android framework settings and package metadata only when explicitly launched."
                )
            }

            item {
                PrivacyGuaranteeCard(
                    title = "Respect for Android Security Sandbox",
                    description = "The app never attempts to bypass Android permission isolation or probe private sandbox directories of other apps. Limitations imposed by the OS are honestly surfaced to you."
                )
            }

            item {
                PrivacyGuaranteeCard(
                    title = "Local Heuristic URL Inspection",
                    description = "URL links are analyzed purely through client-side syntactic heuristics (pattern matching, length, port checks, and punycode). URLs are not pre-fetched or visited behind the scenes."
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PrivacyGuaranteeCard(title: String, description: String) {
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
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SecurityGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
