package com.example.alarmyapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Modern alarm app color palette
private val SunriseOrange = Color(0xFFFF6B35)
private val SunriseOrangeLight = Color(0xFFFF8A5C)
private val DeepNight = Color(0xFF1A1A2E)
private val MidnightBlue = Color(0xFF16213E)
private val SoftPurple = Color(0xFF7B68EE)
private val WarmYellow = Color(0xFFFFD93D)
private val SoftCyan = Color(0xFF6FEDD6)
private val ErrorRed = Color(0xFFFF6B6B)

private val DarkColorScheme = darkColorScheme(
    primary = SunriseOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D2E1F),
    onPrimaryContainer = SunriseOrangeLight,
    secondary = SoftPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D2A4A),
    onSecondaryContainer = Color(0xFFE0DCFF),
    tertiary = SoftCyan,
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF004D45),
    onTertiaryContainer = SoftCyan,
    background = DeepNight,
    onBackground = Color(0xFFE6E1E5),
    surface = MidnightBlue,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF252A40),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF49454F)
)

private val LightColorScheme = lightColorScheme(
    primary = SunriseOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0D6),
    onPrimaryContainer = Color(0xFF3D1E0A),
    secondary = SoftPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF00897B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = Color(0xFF002A26),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F0F7),
    onSurfaceVariant = Color(0xFF49454F),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF79747E)
)

private val AlarmTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun AlarmyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AlarmTypography,
        content = content
    )
}