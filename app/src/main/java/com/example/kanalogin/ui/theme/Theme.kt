package com.example.kanalogin.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kanalogin.menu.userprofile.settings.PreferenceManager
import com.example.kanalogin.menu.userprofile.settings.Theme
import com.example.kanalogin.R

// Custom Font for Japanese theme (Chevin as placeholder, replace with Japanese-inspired font)
val ChevinFontFamily = FontFamily(
    Font(R.font.chevin_bold, FontWeight.Medium)
)

// Custom Typography
val KanaTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = ChevinFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ChevinFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
)

// Light and Dark Color Schemes
private val LightColors = lightColorScheme(
    primary = Salmon_Pink,
    secondary = Wasabi_Green,
    tertiary = Aizome_Blue,
    background = Light_Beige,
    surface = Color.LightGray,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = Tertiary,
    surface = Color.DarkGray,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun KanaLoginTheme(
    darkTheme: Boolean,  // Directly use the darkTheme state here
    dynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    content: @Composable () -> Unit
) {
    // Get context and apply theme based on the darkTheme flag
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    // Apply the theme to the app
    MaterialTheme(
        colorScheme = colorScheme,
        typography = KanaTypography,
        content = content
    )
}


