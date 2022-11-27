/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.juvcarl.shoplist.ui.theme

import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
)
/**
 * Light default gradient colors
 */
val LightDefaultGradientColors = GradientColors(
    primary = md_theme_light_inversePrimary,
    secondary = md_theme_light_secondary,
    tertiary = md_theme_light_tertiary,
    neutral = md_theme_light_outline
)

val DarkDefaultGradientColors = GradientColors(
    primary = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_onPrimary,
    tertiary = md_theme_dark_tertiary,
    neutral = md_theme_dark_outline
)
/**
 * Light Android background theme
 */
val LightAndroidBackgroundTheme = BackgroundTheme(color = md_theme_light_inversePrimary)

/**
 * Dark Android background theme
 */
val DarkAndroidBackgroundTheme = BackgroundTheme(color = md_theme_dark_primaryContainer)


/**
 * Now in Android theme.
 *
 * The order of precedence for the color scheme is: Dynamic color > Android theme > Default theme.
 * Dark theme is independent as all the aforementioned color schemes have light and dark versions.
 * The default theme color scheme is used by default.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param dynamicColor Whether the theme should use a dynamic color scheme (Android 12+ only).
 * @param androidTheme Whether the theme should use the Android theme color scheme.
 */
@Composable
fun ShopListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    androidTheme: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colorScheme: ColorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        androidTheme -> if (darkTheme) DarkColors else LightColors
        else -> if (darkTheme) DarkColors else LightColors
    }

    val defaultGradientColors = GradientColors()
    val gradientColors = when {
        dynamicColor -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                defaultGradientColors
            } else {
                if (darkTheme) defaultGradientColors else LightDefaultGradientColors
            }
        }
        androidTheme -> if (darkTheme) DarkDefaultGradientColors else LightDefaultGradientColors
        else -> if (darkTheme) DarkDefaultGradientColors else LightDefaultGradientColors
    }

    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp
    )
    val backgroundTheme = when {
        dynamicColor -> defaultBackgroundTheme
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
    }

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme
    ) {
        val systemUiController = rememberSystemUiController()

        DisposableEffect(systemUiController, darkTheme) {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !darkTheme
            )

            // setStatusBarColor() and setNavigationBarColor() also exist

            onDispose {}
        }
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NiaTypography,
            content = content
        )
    }
}
