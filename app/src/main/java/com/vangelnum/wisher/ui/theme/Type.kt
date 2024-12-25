package com.vangelnum.wisher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vangelnum.wisher.R

private val InterTight = FontFamily(
    Font(R.font.intertightthin, FontWeight.Thin),
    Font(R.font.intertightextralight, FontWeight.ExtraLight),
    Font(R.font.intertightlight, FontWeight.Light),
    Font(R.font.intertightregular, FontWeight.Normal),
    Font(R.font.intertightmedium, FontWeight.Medium),
    Font(R.font.intertightsemibold, FontWeight.SemiBold),
    Font(R.font.intertightbold, FontWeight.Bold),
    Font(R.font.intertightextrabold, FontWeight.ExtraBold),
    Font(R.font.intertightblack, FontWeight.Black)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterTight,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)