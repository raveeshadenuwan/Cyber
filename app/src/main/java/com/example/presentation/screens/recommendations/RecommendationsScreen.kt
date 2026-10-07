package com.example.presentation.screens.recommendations

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.SecurityRecommendation
import com.example.domain.model.SecuritySeverity
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.SeverityBadge
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

@Composable
fun RecommendationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current

    val recommendations = listOf(
        SecurityRecommendation(
            id = "rec_lock",
            title = "Configure a Secure Screen Lock",
            description = "Enforce PIN, strong alphanumeric passphrase, or biometric fingerprint authentication to prevent unauthorized physical device access.",
            severity = SecuritySeverity.CRITICAL,
            actionLabel = "Lock Screen Settings",
            settingsAction = Settings.ACTION_SECURITY_SETTINGS
        ),
        SecurityRecommendation(
            id = "rec_usb",
            title = "Disable USB Debugging When Not in Use",
            description = "USB Debugging exposes diagnostic interfaces over cable connections, creating vulnerabilities if connected to public charging kiosks or untrusted computers.",
            severity = SecuritySeverity.WARNING,
            actionLabel = "Developer Settings",
            settingsAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        ),
        SecurityRecommendation(
            id = "rec_apps",
            title = "Audit Sensitive App Permissions",
            description = "Regularly inspect applications that have granted access to Camera, Microphone, SMS, Contacts, and Background Location.",
            severity = SecuritySeverity.WARNING,
            actionLabel = "Review Apps in Settings",
            settingsAction = Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS
        ),
        SecurityRecommendation(
            id = "rec_patch",
            title = "Keep Android Security Patches Updated",
            description = "Manufacturers release monthly or quarterly security bulletins addressing newly uncovered vulnerabilities in drivers and Android runtime.",
            severity = SecuritySeverity.WARNING,
            actionLabel = "Check Updates",
            settingsAction = Settings.ACTION_SYSTEM_UPDATE_SETTINGS
        ),
        SecurityRecommendation(
            id = "rec_sideload",
            title = "Restrict Unknown Sideload Sources",
            description = "Only download applications from verified repositories such as Google Play to prevent repackaged trojans and spyware.",
            severity = SecuritySeverity.INFO,
            actionLabel = "App Sources",
            settingsAction = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
        ),
        SecurityRecommendation(
            id = "rec_passwords",
            title = "Adopt High-Entropy Passwords",
            description = "Avoid dictionary words, sequences, and personal identifiers. Generate passwords with at least 16 mixed characters using CyberGuard's offline generator.",
            severity = SecuritySeverity.INFO,
            actionLabel = "Open Password Tool",
            settingsAction = null
        ),
        SecurityRecommendation(
            id = "rec_mfa",
            title = "Enable Multi-Factor Authentication (MFA)",
            description = "Secure your critical Google, banking, and communications accounts using authenticator apps or hardware security keys.",
            severity = SecuritySeverity.INFO,
            actionLabel = "Learn More",
            settingsAction = null
        )
    )

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "HARDENING GUIDE",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("recommendations_screen")
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "ACTIONABLE DEFENSIVE PRACTICES",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(recommendations, key = { it.id }) { rec ->
                RecommendationCard(
                    recommendation = rec,
                    onActionClick = { action ->
                        if (action != null) {
                            openSettings(context, action)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: SecurityRecommendation,
    onActionClick: (String?) -> Unit
) {
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
                SeverityBadge(severity = recommendation.severity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (recommendation.settingsAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = { onActionClick(recommendation.settingsAction) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyberPrimary.copy(alpha = 0.15f),
                        contentColor = CyberPrimary
                    )
                ) {
                    Text(text = recommendation.actionLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private fun openSettings(context: Context, action: String) {
    try {
        val intent = Intent(action).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        } catch (_: Exception) {}
    }
}
