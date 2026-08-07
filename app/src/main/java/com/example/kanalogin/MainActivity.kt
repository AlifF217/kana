package com.example.kanalogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.kanalogin.menu.userprofile.settings.PreferenceManager
import com.example.kanalogin.ui.theme.KanaLoginTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Initialize PreferenceManager
            val context = this@MainActivity
            val preferenceManager = remember { PreferenceManager(context) }
            val navController = rememberNavController()

            // Get the system theme setting
            val systemTheme = isSystemInDarkTheme()

            // Handle theme change state
            var isDarkMode by remember { mutableStateOf(false) }

            // Fetch theme from Firestore initially
            preferenceManager.fetchThemeFromFirestore(
                onSuccess = { theme ->
                    isDarkMode = theme == com.example.kanalogin.menu.userprofile.settings.Theme.DARK
                },
                onFailure = {
                    // Fallback to default light mode on failure
                    isDarkMode = systemTheme
                }
            )

            // Define the logout function
            val onLogout: () -> Unit = {
                FirebaseAuth.getInstance().signOut() // Sign out the user and destroy the session
                // Navigate to the login screen after logout
                navController.navigate("login")
            }


            // Apply the theme dynamically based on the user's preference
            KanaLoginTheme(darkTheme = isDarkMode) {
                MainApp(onLogout = onLogout) // Pass the onLogout function to the MainApp composable
            }
        }
    }
}
