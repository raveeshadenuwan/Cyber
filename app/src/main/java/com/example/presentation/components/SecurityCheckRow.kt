package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecuritySeverity
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.SecurityAmber
import com.example.ui.theme.SecurityBlue
import com.example.ui.theme.SecurityGray
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed

@Composable
fun SecurityCheckRow(
    check: SecurityCheck,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (icon, tint, bg) = when (check.severity) {
        SecuritySeverity.SECURE -> Triple(Icons.Default.Check, SecurityGreen, SecurityGreen.copy(alpha = 0.15f))
        SecuritySeverity.WARNING -> Triple(Icons.Default.Warning, SecurityAmber, SecurityAmber.copy(alpha = 0.15f))
        SecuritySeverity.CRITICAL -> Triple(Icons.Default.Close, SecurityRed, SecurityRed.copy(alpha = 0.15f))
        SecuritySeverity.INFO -> Triple(Icons.Default.Info, SecurityBlue, SecurityBlue.copy(alpha = 0.15f))
        SecuritySeverity.UNAVAILABLE -> Triple(Icons.AutoMirrored.Filled.Help, SecurityGray, SecurityGray.copy(alpha = 0.15f))
    }

    Row(
        modifier = modifier
            .testTag("check_row_${check.id}")
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = check.severity.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = check.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = check.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (check.severity == SecuritySeverity.SECURE) MaterialTheme.colorScheme.onSurfaceVariant else tint
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        SeverityBadge(severity = check.severity)
    }
}
