package com.example.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.SecurityFinding
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

@Composable
fun FindingCard(
    finding: SecurityFinding,
    modifier: Modifier = Modifier,
    onActionClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("finding_card_${finding.id}")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = finding.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                SeverityBadge(severity = finding.severity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = finding.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = finding.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (finding.recommendation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommendation: ${finding.recommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (finding.settingsAction != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = { onActionClick(finding.settingsAction) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyberPrimary.copy(alpha = 0.15f),
                        contentColor = CyberPrimary
                    )
                ) {
                    Text(
                        text = finding.actionLabel ?: "Resolve in Settings",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
