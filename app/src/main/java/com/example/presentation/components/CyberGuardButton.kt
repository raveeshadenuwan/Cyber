package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPrimary

@Composable
fun PrimaryCyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "primary_cyber_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberPrimary,
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = CyberPrimary.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.background,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun SecondaryCyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "secondary_cyber_button"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyberCardBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
