package com.example.kanalogin.menu.userprofile.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class PreferenceManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Set the user's theme preference and update it in Firestore
    fun setTheme(theme: Theme) {
        preferences.edit { putString("theme", theme.name) }

        // Update theme in Firestore under 'user_theme_pref' collection
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it).update("user_theme_pref", theme.name)
        }
    }

    // Get the saved theme. If not set, default to system theme or Firestore preference
    fun getTheme(): Theme {
        val themeName = preferences.getString("theme", null)
        return when {
            themeName != null -> Theme.valueOf(themeName) // User-defined theme from preferences
            else -> Theme.LIGHT // Default to light if no theme preference is set
        }
    }

    // Fetch theme preference from Firestore if available
    fun fetchThemeFromFirestore(onSuccess: (Theme) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val theme = document.getString("user_theme_pref")?.let {
                            Theme.valueOf(it)
                        } ?: Theme.LIGHT // Default to light if no preference set in Firestore
                        onSuccess(theme)
                    } else {
                        onSuccess(Theme.LIGHT) // Default to light if no document found
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
}


enum class Theme {
    LIGHT, DARK
}
