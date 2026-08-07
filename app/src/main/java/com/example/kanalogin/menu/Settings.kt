package com.example.kanalogin.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.menu.accountsettings.*
import com.example.kanalogin.menu.userprofile.settings.PreferenceManager
import com.example.kanalogin.menu.userprofile.settings.PreferenceToggle
import com.example.kanalogin.menu.userprofile.settings.Theme
import com.example.kanalogin.ui.theme.CustomTypography
import com.example.kanalogin.ui.theme.KanaLoginTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }

    // Get current user
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Initialize theme from preferences
    LaunchedEffect(Unit) {
        val currentTheme = preferenceManager.getTheme()
        onThemeChanged(currentTheme == Theme.DARK)
    }

    // Handle theme changes
    val handleThemeChange: (Boolean) -> Unit = { isChecked ->
        onThemeChanged(isChecked)
        val theme = if (isChecked) Theme.DARK else Theme.LIGHT
        preferenceManager.setTheme(theme)
    }

    KanaLoginTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Settings",
                            style = CustomTypography.bodyLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SettingOption(
                        title = "Account",
                        description = "Manage your account settings"
                    )


                    // Change Username
                    TextButton(onClick = {
                        currentUser?.let { user ->
                            navController.navigate("change_username_screen")
                        }
                    }) {
                        Text(text = "Change Username", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
                    }

                    TextButton(onClick = {
                        currentUser?.let { user ->

                            navController.navigate("change_email_screen")
                        }
                    }) {
                        Text(text = "Change Email", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
                    }

                    // Change Password
                    TextButton(onClick = {
                        navController.navigate("change_password_screen")
                    }) {
                        Text(text = "Change Password", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
                    }

                    // Delete Account
                    TextButton(onClick = {
                        currentUser?.let { user ->
                            navController.navigate("delete_account_screen")
                        }
                    }) {
                        Text(text = "Delete Account", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp), color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Preferences Section
                    SettingOption(
                        title = "Preferences",
                        description = "Customize your app experience"
                    )
                    PreferenceToggle(
                        preferenceManager = preferenceManager,
                        onThemeChanged = handleThemeChange
                    )
                }
            }
        )
    }
}

@Composable
fun SettingOption(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 25.sp),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 23.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
