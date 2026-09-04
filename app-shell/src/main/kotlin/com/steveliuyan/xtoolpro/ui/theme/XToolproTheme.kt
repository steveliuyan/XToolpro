@file:Suppress("ktlint:standard:function-naming")

package com.steveliuyan.xtoolpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CanvasLight = Color(0xFFF7FBFC)
private val SurfaceLight = Color(0xFFFFFFFF)
private val TextLight = Color(0xFF27343B)
private val MutedLight = Color(0xFF69757C)
private val BorderLight = Color(0xFFDFE7EA)
private val AccentLight = Color(0xFF1689B8)
private val AccentSoftLight = Color(0xFFE7F5FA)

private val CanvasDark = Color(0xFF1D2529)
private val SurfaceDark = Color(0xFF252F34)
private val TextDark = Color(0xFFEAF2F5)
private val MutedDark = Color(0xFFB6C3C8)
private val BorderDark = Color(0xFF46545A)
private val AccentDark = Color(0xFF73C7E7)
private val AccentSoftDark = Color(0xFF294552)

private val LightColors =
    lightColorScheme(
        primary = AccentLight,
        onPrimary = SurfaceLight,
        primaryContainer = AccentSoftLight,
        onPrimaryContainer = TextLight,
        secondaryContainer = AccentSoftLight,
        onSecondaryContainer = TextLight,
        background = CanvasLight,
        onBackground = TextLight,
        surface = SurfaceLight,
        onSurface = TextLight,
        surfaceVariant = Color(0xFFF1F6F8),
        onSurfaceVariant = MutedLight,
        outline = BorderLight,
        outlineVariant = BorderLight,
        error = Color(0xFFB3261E),
    )

private val DarkColors =
    darkColorScheme(
        primary = AccentDark,
        onPrimary = Color(0xFF003548),
        primaryContainer = AccentSoftDark,
        onPrimaryContainer = TextDark,
        secondaryContainer = AccentSoftDark,
        onSecondaryContainer = TextDark,
        background = CanvasDark,
        onBackground = TextDark,
        surface = SurfaceDark,
        onSurface = TextDark,
        surfaceVariant = Color(0xFF303B40),
        onSurfaceVariant = MutedDark,
        outline = BorderDark,
        outlineVariant = BorderDark,
        error = Color(0xFFFFB4AB),
    )

@Composable
fun XToolproTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        shapes =
            Shapes(
                small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                medium = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                large = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            ),
        content = content,
    )
}
