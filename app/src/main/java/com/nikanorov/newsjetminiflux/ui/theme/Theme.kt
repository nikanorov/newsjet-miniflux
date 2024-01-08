package com.nikanorov.newsjetminiflux.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightThemeColors = lightColors(
    primary = AppColors.Red700,
    primaryVariant = AppColors.Red900,
    onPrimary = Color.White,
    secondary = AppColors.Red700,
    secondaryVariant = AppColors.Red900,
    onSecondary = Color.White,
    error = AppColors.Red800,
    onBackground = Color.Black,

)

private val DarkThemeColors = darkColors(
    primary = AppColors.Red300,
    primaryVariant = AppColors.Red700,
    onPrimary = Color.Black,
    secondary = AppColors.Red300,
    onSecondary = Color.Black,
    error = AppColors.Red200,
    onBackground = Color.White
)

@Composable
fun NewsjetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = NewsjetTypography,
        shapes = NewsjetShapes,
        content = content
    )
}
