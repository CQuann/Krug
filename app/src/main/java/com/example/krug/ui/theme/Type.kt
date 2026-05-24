package com.example.krug.ui.theme

import androidx.compose.material3.Typography

private val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Fonts.Montserrat),
    displayMedium = baseline.displayMedium.copy(fontFamily = Fonts.Montserrat),
    displaySmall = baseline.displaySmall.copy(fontFamily = Fonts.Montserrat),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Fonts.Montserrat),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Fonts.Montserrat),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Fonts.Montserrat),
    titleLarge = baseline.titleLarge.copy(fontFamily = Fonts.Montserrat),
    titleMedium = baseline.titleMedium.copy(fontFamily = Fonts.Montserrat),
    titleSmall = baseline.titleSmall.copy(fontFamily = Fonts.Montserrat),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = Fonts.SourceSansPro),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = Fonts.SourceSansPro),
    bodySmall = baseline.bodySmall.copy(fontFamily = Fonts.SourceSansPro),
    labelLarge = baseline.labelLarge.copy(fontFamily = Fonts.SourceSansPro),
    labelMedium = baseline.labelMedium.copy(fontFamily = Fonts.SourceSansPro),
    labelSmall = baseline.labelSmall.copy(fontFamily = Fonts.SourceSansPro),
)