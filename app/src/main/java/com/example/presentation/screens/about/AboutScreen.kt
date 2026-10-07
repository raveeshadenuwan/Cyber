package com.example.presentation.screens.about

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.presentation.components.CyberGuardTopBar
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

const val APP_DEVELOPER_NAME = "Raveesha Denuwan"
const val APP_VERSION_NAME = "1.0.0"

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "ABOUT",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                showSettingsAction = false
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .testTag("about_screen")
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = CyberPrimary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "CYBERGUARD MOBILE",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Version $APP_VERSION_NAME",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lead Developer: $APP_DEVELOPER_NAME",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "CORE ARCHITECTURE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CyberGuard Mobile is built with Kotlin and Jetpack Compose using modern MVVM clean architecture, Room persistence, and Android framework security APIs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "It operates strictly on-device without cloud dependencies, providing immediate posture evaluation, permission auditing, entropy analysis, and heuristic URL verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "OPEN SOURCE TECHNOLOGIES",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TechStackItem("Jetpack Compose & Material 3", "Modern declarative UI toolkit")
                        TechStackItem("AndroidX Room & SQLite", "Robust offline structured database")
                        TechStackItem("Kotlin Coroutines & Flow", "Asynchronous reactive state streams")
                        TechStackItem("AndroidX Navigation Compose", "Type-safe declarative navigation")
                        TechStackItem("Android Framework Security APIs", "Keyguard, Connectivity, & Settings APIs")
                    }
                }
            }
        }
    }
}

@Composable
private fun TechStackItem(title: String, desc: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
