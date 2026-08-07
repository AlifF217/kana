package com.example.kanalogin.sayit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.menu.userprofile.settings.PreferenceManager
import com.example.kanalogin.menu.userprofile.settings.PreferenceToggle
import com.example.kanalogin.menu.userprofile.settings.Theme
import com.example.kanalogin.ui.theme.CustomTypography
import com.example.kanalogin.ui.theme.KanaLoginTheme

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SayIt(
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }

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
                            "Say it!",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    }
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
                    SayItButton(
                        title = "Training",
                        description = "Train your Japanese pronounciations and speaking skills without losing any EXP!",
                        onClick = { navController.navigate("sayit_training") }
                    )
                    SayItButton(
                        title = "Basic Course",
                        description = "Test yourself in your Japanese speaking skills using this course! Are you up to the challenge?",
                        onClick = { navController.navigate("sayit_basic_course") }
                    )
                }
            }
        )
    }
}

@Composable
fun SayItButton(title: String, description: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), // Change to CircleShape or other shapes
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF17175),    // Background color
            contentColor = Color.White     // Text color
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
