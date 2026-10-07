package com.example.presentation.screens.history

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ScanEntity
import com.example.presentation.components.CyberGuardTopBar
import com.example.presentation.components.EmptyStateView
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
fun ScanHistoryScreen(
    viewModel: ScanHistoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val scans by viewModel.scansHistory.collectAsStateWithLifecycle()
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "AUDIT LOGS",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        if (scans.isEmpty()) {
            EmptyStateView(
                title = "No Scan History",
                message = "Run your first security audit from the Scan tab to record device security posture.",
                icon = Icons.Default.History,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .testTag("scan_history_screen")
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREVIOUS ASSESSMENTS (${scans.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = SecurityRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear All", color = SecurityRed)
                        }
                    }
                }

                items(scans, key = { it.id }) { scan ->
                    ScanHistoryItemCard(
                        scan = scan,
                        onDelete = { viewModel.deleteScan(scan.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                icon = {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = SecurityRed)
                },
                title = { Text("Delete All Scan History?") },
                text = { Text("Are you sure you want to delete all historical security audit records? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAllHistory()
                        showClearConfirmDialog = false
                    }) {
                        Text("Delete", color = SecurityRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun ScanHistoryItemCard(
    scan: ScanEntity,
    onDelete: () -> Unit
) {
    val sdfDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(scan.timestamp)

    val scoreColor = when {
        scan.score >= 90 -> SecurityGreen
        scan.score >= 75 -> CyberPrimary
        scan.score >= 50 -> SecurityAmber
        else -> SecurityRed
    }

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular score badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(scoreColor.copy(alpha = 0.15f))
                    .border(1.5.dp, scoreColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${scan.score}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = scoreColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${sdfDate.format(date)} at ${sdfTime.format(date)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = scan.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${scan.passedCount} passed · ${scan.warningCount} warnings · ${scan.criticalCount} critical",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete record",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
