package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.model.SecuritySeverity
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityAmberBg
import com.example.ui.theme.SecurityBlue
import com.example.ui.theme.SecurityBlueBg
import com.example.ui.theme.SecurityGray
import com.example.ui.theme.SecurityGrayBg
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityGreenBg
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.SecurityRedBg

@Composable
fun SeverityBadge(
    severity: SecuritySeverity,
    modifier: Modifier = Modifier
) {
    val (bg, border, contentColor, icon) = when (severity) {
        SecuritySeverity.CRITICAL -> Quad(SecurityRedBg.copy(alpha = 0.4f), SecurityRed, SecurityRed, Icons.Default.Error)
        SecuritySeverity.WARNING -> Quad(SecurityAmberBg.copy(alpha = 0.4f), SecurityAmber, SecurityAmber, Icons.Default.Warning)
        SecuritySeverity.SECURE -> Quad(SecurityGreenBg.copy(alpha = 0.4f), SecurityGreen, SecurityGreen, Icons.Default.CheckCircle)
        SecuritySeverity.INFO -> Quad(SecurityBlueBg.copy(alpha = 0.4f), SecurityBlue, SecurityBlue, Icons.Default.Info)
        SecuritySeverity.UNAVAILABLE -> Quad(SecurityGrayBg.copy(alpha = 0.4f), SecurityGray, SecurityGray, Icons.AutoMirrored.Filled.Help)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("severity_badge_${severity.name.lowercase()}")
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = severity.label,
            tint = contentColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = severity.label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
