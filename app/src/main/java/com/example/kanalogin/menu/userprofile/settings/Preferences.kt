package com.example.kanalogin.menu.userprofile.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf

@Composable
fun PreferenceToggle(preferenceManager: PreferenceManager, onThemeChanged: (Boolean) -> Unit) {
    // Get the current theme based on the preference or system theme
    val currentTheme = preferenceManager.getTheme()

    // Create a mutable state to keep track of the current theme
    val themeState = remember { mutableStateOf(currentTheme == Theme.DARK) }

    // Automatically fetch theme from Firestore and update the UI when the app starts
    LaunchedEffect(Unit) {
        preferenceManager.fetchThemeFromFirestore(
            onSuccess = { theme ->
                // Sync UI with the theme fetched from Firestore
                themeState.value = theme == Theme.DARK
                onThemeChanged(themeState.value)
            },
            onFailure = {
                // Fallback to default theme if Firestore fetch fails
                themeState.value = currentTheme == Theme.DARK
                onThemeChanged(themeState.value)
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dark Mode",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = themeState.value,
            onCheckedChange = { isChecked ->
                // Update the state and preferences immediately
                themeState.value = isChecked
                val theme = if (isChecked) Theme.DARK else Theme.LIGHT
                preferenceManager.setTheme(theme)
                onThemeChanged(isChecked)
            }
        )
    }
}