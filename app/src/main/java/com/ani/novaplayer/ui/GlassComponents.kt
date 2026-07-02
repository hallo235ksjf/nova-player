package com.ani.novaplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ani.novaplayer.ui.theme.GlassBorder
import com.ani.novaplayer.ui.theme.GlassSurface

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(GlassSurface, GlassSurface.copy(alpha = 0.08f))
                ),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .border(1.dp, GlassBorder, RoundedCornerShape(cornerRadius.dp))
    ) {
        content()
    }
}

fun accentGradient() = Brush.horizontalGradient(
    listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF10B981))
)
