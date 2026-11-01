package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonGlow
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.VoidSurface

fun Modifier.glassPanel(
    cornerRadius: Dp = 16.dp,
    borderColor: Color = GlassBorder,
    backgroundColor: Color = VoidSurface.copy(alpha = 0.75f)
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(backgroundColor)
    .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))

fun Modifier.neonGlowBorder(
    cornerRadius: Dp = 16.dp,
    isActive: Boolean = true
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .border(
        width = if (isActive) 1.5.dp else 1.dp,
        brush = if (isActive) {
            Brush.linearGradient(
                colors = listOf(NeonViolet, NeonGlow, Color(0xFF06B6D4))
            )
        } else {
            Brush.linearGradient(
                colors = listOf(GlassBorder, GlassBorder)
            )
        },
        shape = RoundedCornerShape(cornerRadius)
    )

@Composable
fun Modifier.scanlineAnimation(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    this.drawWithContent {
        drawContent()
        val y = size.height * offsetY
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    NeonViolet.copy(alpha = 0.6f),
                    NeonGlow.copy(alpha = 0.9f),
                    NeonViolet.copy(alpha = 0.6f),
                    Color.Transparent
                ),
                startY = y - 40f,
                endY = y + 40f
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 6f
        )
    }
}
