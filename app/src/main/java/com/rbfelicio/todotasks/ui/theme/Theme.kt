// /Users/felicio/AndroidStudioProjects/ToDoTasks/app/src/main/java/com/rbfelicio/todotasks/ui/theme/Theme.kt
package com.rbfelicio.todotasks.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppLightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryVariantGreen,
    onPrimaryContainer = OnPrimaryGreen,
    secondary = SecondaryGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryVariantGreen,
    onSecondaryContainer = OnSecondaryGreen,
    tertiary = PrimaryGreen,
    onTertiary = OnPrimaryGreen,
    error = ErrorRed,
    onError = OnErrorRed,
    background = BackgroundGreenish,
    onBackground = OnBackgroundGreenish,
    surface = SurfaceGreenish,
    onSurface = OnSurfaceGreenish,
    surfaceVariant = SurfaceVariantGreenish,
    onSurfaceVariant = OnSurfaceVariantGreenish

)


private val AppDarkColorScheme = darkColorScheme(
    primary = PrimaryVariantGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = OnPrimaryGreen,
    secondary = SecondaryVariantGreen,
    onSecondary = OnSecondaryGreen,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = OnSecondaryGreen,
    tertiary = PrimaryVariantGreen,
    onTertiary = OnPrimaryGreen,
    error = ErrorRed,
    onError = OnErrorRed,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD)
)

@Composable
fun ToDoTasksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
