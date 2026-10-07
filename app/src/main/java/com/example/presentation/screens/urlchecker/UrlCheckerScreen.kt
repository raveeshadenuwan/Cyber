package com.example.presentation.screens.urlchecker

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.UrlAnalysisResult
import com.example.domain.model.UrlRiskLevel
import com.example.domain.model.UrlSecurityFlag
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.PrimaryCyberButton
import com.example.presentation.components.SecondaryCyberButton
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed

@Composable
fun UrlCheckerScreen(
    viewModel: UrlCheckerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val sampleUrls = listOf(
        "https://google.com",
        "http://192.168.1.1/login",
        "https://secure-login-example.com",
        "http://safe-bank.com@malicious-host.net",
        "https://bit.ly/update-token",
        "http://portal.account-verify.online"
    )

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "URL DEFENSE CHECK",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("url_checker_screen")
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explanatory Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Heuristic URL Safety Inspector",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Evaluates links offline for homograph impersonation, raw IP targets, obfuscated ports, and deceptive authority prefixes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Input section
            item {
                OutlinedTextField(
                    value = state.inputUrl,
                    onValueChange = { viewModel.onUrlInputChanged(it) },
                    label = { Text("Destination URL") },
                    placeholder = { Text("https://example.com/login") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier
                        .testTag("url_input_field")
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCardBorder
                    ),
                    singleLine = true
                )
            }

            // Quick Samples
            item {
                Text(
                    text = "SAMPLE LINKS TO TEST",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sampleUrls) { sample ->
                        ElevatedAssistChip(
                            onClick = {
                                viewModel.analyzeUrl(sample)
                            },
                            label = { Text(sample.substringBefore('/', sample).take(24)) }
                        )
                    }
                }
            }

            item {
                PrimaryCyberButton(
                    text = "ANALYZE LINK",
                    icon = Icons.Default.Search,
                    onClick = { viewModel.analyzeUrl() },
                    testTag = "analyze_url_button"
                )
            }

            // Analysis Results
            if (state.analysisResult != null) {
                val res = state.analysisResult!!

                item {
                    UrlResultCard(result = res)
                }

                if (res.flags.isNotEmpty()) {
                    item {
                        Text(
                            text = "IDENTIFIED RISK SIGNALS (${res.flags.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(res.flags) { flag ->
                        UrlFlagCard(flag = flag)
                    }
                }

                // Explicit Browser Open Button
                item {
                    SecondaryCyberButton(
                        text = "Open Link in Browser",
                        icon = Icons.Default.OpenInBrowser,
                        onClick = { viewModel.requestOpenBrowser() },
                        testTag = "open_in_browser_button"
                    )
                }
            }
        }

        // Confirmation Dialog before opening external browser
        if (state.showBrowserConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissBrowserConfirm() },
                icon = {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = SecurityAmber)
                },
                title = { Text("Open in External Browser?") },
                text = {
                    Text(
                        "You are about to open:\n\n${state.analysisResult?.url}\n\nCyberGuard does not guarantee external websites are benign. Make sure you trust the destination."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmOpenBrowser() }) {
                        Text("Open Anyway", color = CyberPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissBrowserConfirm() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun UrlResultCard(result: UrlAnalysisResult) {
    val (color, icon) = when (result.riskLevel) {
        UrlRiskLevel.SAFE_LOOKING -> Pair(SecurityGreen, Icons.Default.CheckCircle)
        UrlRiskLevel.SUSPICIOUS -> Pair(SecurityAmber, Icons.Default.Warning)
        UrlRiskLevel.HIGH_RISK -> Pair(SecurityRed, Icons.Default.Error)
        UrlRiskLevel.UNABLE_TO_DETERMINE -> Pair(Color.Gray, Icons.Default.Info)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = result.riskLevel.label.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = result.verdict,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = result.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mandatory Disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Notice: This result is a heuristic assessment and does not guarantee that a website is safe.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UrlFlagCard(flag: UrlSecurityFlag) {
    val color = if (flag.isCritical) SecurityRed else SecurityAmber

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
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (flag.isCritical) Icons.Default.Error else Icons.Default.Warning,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = flag.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = flag.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
