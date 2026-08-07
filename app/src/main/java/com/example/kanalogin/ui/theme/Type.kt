package com.example.kanalogin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kanalogin.R


// Define Chevin Medium font
val ChevinFont = FontFamily(
    Font(R.font.chevin_bold, FontWeight.Medium)
)

// Custom Typography
val CustomTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = ChevinFont,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontFamily = ChevinFont,
        fontWeight = FontWeight.Bold
    )
)
