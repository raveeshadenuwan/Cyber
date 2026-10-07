package com.example.presentation.screens.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.PasswordStrength
import com.example.domain.security.GeneratorPreset
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
fun PasswordToolsScreen(
    viewModel: PasswordViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Secure Generator

    Scaffold(
        topBar = {
            CyberGuardTopBar(
                title = "SECURE PASSWORDS",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .testTag("password_screen")
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Guarantee Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zero-Knowledge: Generated locally with SecureRandom. Never saved or transmitted.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CyberPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Strength Meter", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_password_analyzer")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Password Generator", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_password_generator")
                )
            }

            if (selectedTab == 0) {
                AnalyzerTabContent(
                    state = state,
                    onPasswordChanged = { viewModel.onPasswordInputChanged(it) }
                )
            } else {
                SecureGeneratorTabContent(
                    state = state,
                    onLengthChanged = { viewModel.onGenLengthChanged(it) },
                    onToggleUpper = { viewModel.toggleUpper(it) },
                    onToggleLower = { viewModel.toggleLower(it) },
                    onToggleDigits = { viewModel.toggleDigits(it) },
                    onToggleSymbols = { viewModel.toggleSymbols(it) },
                    onToggleExcludeAmbiguous = { viewModel.toggleExcludeAmbiguous(it) },
                    onApplyPreset = { viewModel.applyPreset(it) },
                    onGenerateNew = { viewModel.generateNewPassword() },
                    onCopy = { viewModel.copyToClipboard(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SecureGeneratorTabContent(
    state: PasswordUiState,
    onLengthChanged: (Int) -> Unit,
    onToggleUpper: (Boolean) -> Unit,
    onToggleLower: (Boolean) -> Unit,
    onToggleDigits: (Boolean) -> Unit,
    onToggleSymbols: (Boolean) -> Unit,
    onToggleExcludeAmbiguous: (Boolean) -> Unit,
    onApplyPreset: (GeneratorPreset) -> Unit,
    onGenerateNew: () -> Unit,
    onCopy: (String) -> Unit
) {
    val info = state.generatedInfo

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Output Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CRYPTOGRAPHIC RANDOM KEY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyberPrimary
                        )

                        // Entropy Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SecurityGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${info.entropyBits.toInt()} BITS ENTROPY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SecurityGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password text container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = info.password,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Breakdown chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CompositionBadge("Upper: ${info.uppercaseCount}")
                        CompositionBadge("Lower: ${info.lowercaseCount}")
                        CompositionBadge("Digits: ${info.digitsCount}")
                        CompositionBadge("Symbols: ${info.symbolsCount}")
                        CompositionBadge("Est. crack: ${info.crackTimeEstimate}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryCyberButton(
                            text = "Copy",
                            icon = Icons.Default.ContentCopy,
                            onClick = { onCopy(info.password) },
                            modifier = Modifier.weight(1f),
                            testTag = "copy_password_button"
                        )
                        PrimaryCyberButton(
                            text = "Regenerate",
                            icon = Icons.Default.Refresh,
                            onClick = onGenerateNew,
                            modifier = Modifier.weight(1f),
                            testTag = "regenerate_password_button"
                        )
                    }

                    if (state.copyMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SecurityGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.copyMessage,
                                style = MaterialTheme.typography.labelSmall,
                                color = SecurityGreen
                            )
                        }
                    }
                }
            }
        }

        // Quick Presets
        item {
            Column {
                Text(
                    text = "SECURITY PRESETS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(GeneratorPreset.values()) { preset ->
                        FilterChip(
                            selected = state.activePreset == preset,
                            onClick = { onApplyPreset(preset) },
                            label = { Text(preset.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = CyberPrimary
                            )
                        )
                    }
                }
            }
        }

        // Length Customizer
        item {
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
                            text = "PASSWORD LENGTH",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onLengthChanged(state.genLength - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease length")
                            }
                            Text(
                                text = "${state.genLength}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CyberPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { onLengthChanged(state.genLength + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase length")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = state.genLength.toFloat(),
                        onValueChange = { onLengthChanged(it.toInt()) },
                        valueRange = 6f..64f,
                        steps = 58,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberPrimary,
                            activeTrackColor = CyberPrimary,
                            inactiveTrackColor = CyberSurfaceVariant
                        )
                    )
                }
            }
        }

        // Character Sets Config
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
                        text = "CHARACTER COMPOSITION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GeneratorOptionCheckbox(
                        label = "Uppercase Letters (A–Z)",
                        example = "A, B, C, D...",
                        checked = state.genUpper,
                        onCheckedChange = onToggleUpper
                    )
                    GeneratorOptionCheckbox(
                        label = "Lowercase Letters (a–z)",
                        example = "a, b, c, d...",
                        checked = state.genLower,
                        onCheckedChange = onToggleLower
                    )
                    GeneratorOptionCheckbox(
                        label = "Numbers (0–9)",
                        example = "0, 1, 2, 3...",
                        checked = state.genDigits,
                        onCheckedChange = onToggleDigits
                    )
                    GeneratorOptionCheckbox(
                        label = "Special Symbols (!@#$)",
                        example = "! @ # $ % ^ & * ( ) _ + - =",
                        checked = state.genSymbols,
                        onCheckedChange = onToggleSymbols
                    )
                    GeneratorOptionCheckbox(
                        label = "Exclude Ambiguous Characters",
                        example = "Excludes lookalike glyphs: 0, O, 1, l, I",
                        checked = state.genExcludeAmbiguous,
                        onCheckedChange = onToggleExcludeAmbiguous
                    )
                }
            }
        }

        // Recent Session Keys (prevents losing a regenerated key)
        if (state.sessionHistory.size > 1) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RECENT GENERATED KEYS (SESSION RAM ONLY)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.sessionHistory.drop(1).forEach { prevPassword ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = prevPassword,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    IconButton(
                                        onClick = { onCopy(prevPassword) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy password",
                                            tint = CyberPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CompositionBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .border(0.5.dp, CyberCardBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GeneratorOptionCheckbox(
    label: String,
    example: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = CyberPrimary,
                checkmarkColor = MaterialTheme.colorScheme.background
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = example,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AnalyzerTabContent(
    state: PasswordUiState,
    onPasswordChanged: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val result = state.analysisResult

    val strengthColor by animateColorAsState(
        targetValue = when (result.strength) {
            PasswordStrength.VERY_STRONG -> SecurityGreen
            PasswordStrength.STRONG -> CyberPrimary
            PasswordStrength.FAIR -> SecurityAmber
            PasswordStrength.WEAK -> Color(0xFFF97316)
            PasswordStrength.VERY_WEAK -> SecurityRed
        },
        label = "strength_color"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = state.inputPassword,
                onValueChange = onPasswordChanged,
                label = { Text("Test Password") },
                placeholder = { Text("Enter a password to evaluate...") },
                modifier = Modifier
                    .testTag("password_input_field")
                    .fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = CyberCardBorder
                ),
                singleLine = true
            )
        }

        // Strength Meter Card
        item {
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
                            text = "STRENGTH EVALUATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(strengthColor.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = result.strength.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = strengthColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { (result.score / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = strengthColor,
                        trackColor = CyberSurfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Estimated Crack Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = result.crackTimeEstimate,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = strengthColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Entropy Estimate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${result.entropyBits.toInt()} bits",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Requirements Checklist
        item {
            Text(
                text = "SECURITY ATTRIBUTES",
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
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChecklistRow(label = "At least 12 characters (${result.length})", isMet = result.length >= 12)
                    ChecklistRow(label = "Uppercase letters (A-Z)", isMet = result.hasUppercase)
                    ChecklistRow(label = "Lowercase letters (a-z)", isMet = result.hasLowercase)
                    ChecklistRow(label = "Numbers (0-9)", isMet = result.hasDigits)
                    ChecklistRow(label = "Special symbols (!@#$%^&*)", isMet = result.hasSymbols)
                    ChecklistRow(label = "No consecutive repeated patterns", isMet = !result.hasRepeatedChars)
                    ChecklistRow(label = "No common sequences (e.g., 1234, qwerty)", isMet = !result.hasSequentialChars)
                }
            }
        }

        // Actionable Feedback
        if (result.feedback.isNotEmpty()) {
            item {
                Text(
                    text = "HARDENING TIPS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(result.feedback.size) { index ->
                Text(
                    text = "• ${result.feedback[index]}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ChecklistRow(label: String, isMet: Boolean) {
    val color = if (isMet) SecurityGreen else MaterialTheme.colorScheme.onSurfaceVariant
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}
