package com.ani.novaplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val NovaColorScheme = darkColorScheme(
    primary = Indigo,
    secondary = Violet,
    tertiary = NeonGreen,
    background = BgDeep,
    surface = BgSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

val NovaTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = TextPrimary),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, color = TextPrimary),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextSecondary),
    labelSmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp, color = TextSecondary),
)

@Composable
fun NovaPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NovaColorScheme,
        typography = NovaTypography,
        content = content
    )
}
