package com.example.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SecurityScore
import com.example.domain.model.SecurityStatus
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
fun SecurityScoreCard(
    score: SecurityScore?,
    lastScanTime: Long?,
    modifier: Modifier = Modifier
) {
    val currentScore = score?.score ?: 0
    val targetProgress = (currentScore / 100f).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "score_progress"
    )

    val status = score?.status ?: SecurityStatus.CRITICAL
    val statusColor = when (status) {
        SecurityStatus.EXCELLENT -> SecurityGreen
        SecurityStatus.GOOD -> CyberPrimary
        SecurityStatus.NEEDS_ATTENTION -> SecurityAmber
        SecurityStatus.POOR -> Color(0xFFF97316)
        SecurityStatus.CRITICAL -> SecurityRed
    }

    val formattedTime = if (lastScanTime != null && lastScanTime > 0) {
        val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
        sdf.format(Date(lastScanTime))
    } else {
        "Never Scanned"
    }

    Card(
        modifier = modifier
            .testTag("security_score_card")
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Status",
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SECURITY POSTURE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (score != null) status.displayName.uppercase() else "PENDING",
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Circular Meter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    // Background track
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = (size.minDimension - strokeWidth) / 2,
                        style = Stroke(width = strokeWidth)
                    )
                    // Animated arc
                    drawArc(
                        color = statusColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (score != null) "${(animatedProgress * 100).toInt()}" else "--",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "OUT OF 100",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Score metrics breakdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricItem(label = "Passed", value = "${score?.passedCount ?: 0}", color = SecurityGreen)
                MetricItem(label = "Warnings", value = "${score?.warningCount ?: 0}", color = SecurityAmber)
                MetricItem(label = "Critical", value = "${score?.criticalCount ?: 0}", color = SecurityRed)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Last Scan: $formattedTime",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
