package com.example.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .testTag("splash_screen")
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale.value)
                    .size(110.dp)
                    .background(CyberPrimary.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, CyberPrimary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "CyberGuard Shield",
                    tint = CyberPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CYBERGUARD",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mobile Security Assessment",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
