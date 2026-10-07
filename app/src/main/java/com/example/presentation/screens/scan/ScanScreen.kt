package com.example.presentation.screens.scan

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.SecuritySeverity
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.ErrorStateView
import com.example.presentation.components.FindingCard
import com.example.presentation.components.PrimaryCyberButton
import com.example.presentation.components.SecondaryCyberButton
import com.example.presentation.components.SecurityCheckRow
import com.example.presentation.components.SecurityScoreCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.SecurityGreen

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onNavigateToRecommendations: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.scanState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "SECURITY AUDIT",
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .testTag("scan_screen")
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val s = state) {
                is ScanState.Idle -> {
                    ScanIdleView(
                        onStartScan = { viewModel.startScan() }
                    )
                }

                is ScanState.Scanning -> {
                    ScanProgressView(
                        phaseText = s.phaseText,
                        progress = s.progress
                    )
                }

                is ScanState.Completed -> {
                    ScanCompletedView(
                        result = s.result,
                        onScanAgain = { viewModel.startScan() },
                        onViewRecommendations = onNavigateToRecommendations,
                        onOpenSettings = { action -> viewModel.openSystemSettings(action) }
                    )
                }

                is ScanState.Error -> {
                    ErrorStateView(
                        errorMessage = s.message,
                        onRetry = { viewModel.startScan() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanIdleView(
    onStartScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(130.dp)
                .background(CyberPrimary.copy(alpha = 0.08f), CircleShape)
                .border(1.5.dp, CyberPrimary.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = "Radar Scan",
                tint = CyberPrimary,
                modifier = Modifier.size(68.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Device Security Scan",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Perform an in-depth audit of lock screen configuration, developer debug ports, sensitive app permissions, and network exposures.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        PrimaryCyberButton(
            text = "START SCAN",
            icon = Icons.Default.Radar,
            onClick = onStartScan,
            testTag = "scan_start_button"
        )
    }
}

@Composable
private fun ScanProgressView(
    phaseText: String,
    progress: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(pulseScale)
                .size(140.dp)
                .background(CyberPrimary.copy(alpha = 0.12f), CircleShape)
                .border(2.dp, CyberPrimary, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Scanning",
                tint = CyberPrimary,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Auditing Device Security",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = phaseText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = CyberPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = CyberPrimary,
            trackColor = CyberSurfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${(progress * 100).toInt()}% completed",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ScanCompletedView(
    result: com.example.domain.model.SecurityScanResult,
    onScanAgain: () -> Unit,
    onViewRecommendations: () -> Unit,
    onOpenSettings: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SecurityScoreCard(
                score = result.score,
                lastScanTime = result.timestamp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryCyberButton(
                    text = "Scan Again",
                    icon = Icons.Default.Refresh,
                    onClick = onScanAgain,
                    modifier = Modifier.weight(1f),
                    testTag = "scan_again_button"
                )
                SecondaryCyberButton(
                    text = "Hardening Guide",
                    icon = Icons.Default.Lightbulb,
                    onClick = onViewRecommendations,
                    modifier = Modifier.weight(1f),
                    testTag = "scan_recommendations_button"
                )
            }
        }

        if (result.findings.isNotEmpty()) {
            item {
                Text(
                    text = "SECURITY FINDINGS (${result.findings.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(result.findings, key = { it.id }) { finding ->
                FindingCard(
                    finding = finding,
                    onActionClick = onOpenSettings
                )
            }
        }

        item {
            Text(
                text = "PASSED CHECKS & VERIFICATIONS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val passedChecks = result.checks.filter { it.severity == SecuritySeverity.SECURE }
        items(passedChecks, key = { it.id }) { check ->
            SecurityCheckRow(check = check)
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
