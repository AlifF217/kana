package com.example.kanalogin

import android.R.drawable.ic_menu_camera
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.menu.userprofile.DrawableMappings.getDrawableId
import com.example.kanalogin.ui.theme.CustomTypography
import com.example.kanalogin.ui.theme.KanaLoginTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import java.util.UUID

// Upload profile picture to Firebase Storage
fun uploadProfilePicture(imageUri: Uri, context: android.content.Context, onUrlRetrieved: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
            .child("profile_pictures")
            .child("${UUID.randomUUID()}.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onUrlRetrieved(downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }
}

// Save profile picture URL to Firestore
fun saveProfilePictureUrlToFirestore(url: String, context: android.content.Context) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (user != null) {
        val userRef = db.collection("users").document(user.uid)

        userRef.update("profilePictureUrl", url)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile picture URL", Toast.LENGTH_SHORT).show()
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(onLogout: () -> Unit, navController: NavController) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var imageName by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }
    var userLevel by remember { mutableStateOf<Long?>(null) }
    var userRank by remember { mutableStateOf<String?>(null) }
    var userStreak by remember { mutableStateOf<Long?>(null) }
    var userTrophies by remember { mutableStateOf<Long?>(null) }
    var usercurrentEXP by remember { mutableStateOf<Long?>(null) }
    var usernextEXP by remember { mutableStateOf<Long?>(null) }

    // Dropdown menu expanded state
    var expanded by remember { mutableStateOf(false) }

    val (themeState, setThemeState) = remember { mutableStateOf(false) }

// Handle the back press to exit the app
    BackHandler {
        // Exits the app when the back button is pressed
        (context as? Activity)?.finish()
    }

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    imageName = document.getString("userpfp") // Fetch profile picture name
                    userName = document.getString("username")
                    userEmail = document.getString("email")
                    userLevel = document.getLong("level")
                    userRank = document.getString("rank")
                    userStreak = document.getLong("streak")
                    userTrophies = document.getLong("trophies")
                    usercurrentEXP = document.getLong("currentExp")
                    usernextEXP = document.getLong("requiredExp")

                    // Fetch user theme preference and set themeState
                    val themePref = document.getString("user_theme_pref")
                    setThemeState(when (themePref) {
                        "DARK" -> true  // Dark theme
                        "LIGHT" -> false // Light theme
                        else -> false // Default to light theme if no preference
                    })
                }
            }
        }
    }

    // Set the theme based on themeState (false -> light, true -> dark)
    val colors = if (themeState) {
        darkColorScheme() // Define dark theme
    } else {
        lightColorScheme() // Define light theme
    }

    KanaLoginTheme(darkTheme = themeState) {
    Scaffold(
        topBar = {
            // TopAppBar with kebab menu
            TopAppBar(
                title = { Text(text = "Kana", color = MaterialTheme.colorScheme.onBackground, style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)) },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.onBackground)
                    }

                    // Dropdown menu with options
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Settings",
                                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                        style = CustomTypography.bodyLarge.copy(fontSize = 16.sp)
                                    )
                                }
                            },
                            onClick = {
                                expanded = false
                                // Navigate to Settings screen

                                    navController.navigate("settings")

                            },
                            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenuItem(
                            text = {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Feedback",
                                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                        style = CustomTypography.bodyLarge.copy(fontSize = 16.sp)
                                    )
                                }
                            },
                            onClick = {
                                expanded = false
                                // Navigate to Feedback screen
                                navController.navigate("feedback")
                            },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenuItem(
                            text = {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Share Progress",
                                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                        style = CustomTypography.bodyLarge.copy(fontSize = 16.sp)
                                    )
                                }
                            },
                            onClick = {
                                expanded = false
                                // Navigate to Progress Sharing screen
                                navController.navigate("progressSharing/${userName}/${userEmail}")
                            },
                            leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Logout",
                                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                        style = CustomTypography.bodyLarge.copy(fontSize = 16.sp)
                                    )
                                }
                            },
                            onClick = {
                                onLogout() // Trigger logout action
                                expanded = false
                            },
                            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Profile Picture Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display selected profile picture based on imageName
                        imageName?.let {
                            Image(
                                painter = painterResource(id = getDrawableId(it)),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .clickable { navController.navigate("profilePicture") }, // Navigate to Profile Picture screen
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            // Default image if no profile picture is set
                            Image(
                                painter = painterResource(id = ic_menu_camera),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .clickable { navController.navigate("profilePicture") },
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Second column: Username and Email with a 1x6 layout
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = userName ?: "No Username", style = CustomTypography.bodyLarge.copy(fontSize = 20.sp))
                            Text(text = userEmail ?: "No Email", style = CustomTypography.bodyLarge.copy(fontSize = 16.sp))
                            Text(text = userRank ?:"Rank", style = CustomTypography.bodyLarge.copy(fontSize = 14.sp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Level",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Streak",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = "Trophies",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "${userLevel ?: "N/A"}",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${userStreak ?: "N/A"}",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = "${userTrophies ?: "N/A"}",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(1.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {

                                    ProgressBarRepresentation(
                                        usercurrentEXP.toString(),
                                        usernextEXP.toString(),
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "${usercurrentEXP ?: "N/A"} / ${usernextEXP ?: "N/A"} to the next level",
                                        style = CustomTypography.bodyLarge.copy(fontSize = 14.sp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    // Adding verticalScroll to allow scrolling
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()), // This makes the Column scrollable
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("flashcard") }, // Navigate to page 1
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF01CCFF),    // Background color
                                    contentColor = Color.White      // Text color
                                )
                            ) {
                                Text("Flash Card", color = Color.White, style = CustomTypography.bodyLarge.copy(fontSize = 30.sp))
                            }
                            Button(
                                onClick = { navController.navigate("buildasentence") }, // Navigate to page 2
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7DD956),    // Background color
                                    contentColor = Color.White      // Text color
                                )
                            ) {
                                Text("Build-a-"+"\n"+"Sentence", color = Color.White, style = CustomTypography.bodyLarge.copy(fontSize = 22.sp))
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("sayit") }, // Navigate to page 3
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF17175),    // Background color
                                    contentColor = Color.White      // Text color
                                )
                            ) {
                                Text(
                                    "Say it!",
                                    color = Color.White,
                                    style = CustomTypography.bodyLarge.copy(fontSize = 30.sp)
                                )
                            }
                            Button(
                                onClick = { navController.navigate("storytime") }, // Navigate to page 4
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF8C00),    // Background color
                                    contentColor = Color.White      // Text color
                                )
                            ) {
                                Text(
                                    "Story Time",
                                    color = Color.White,
                                    style = CustomTypography.bodyLarge.copy(fontSize = 30.sp)
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("letters") }, // Navigate to page 1
                                modifier = Modifier
                                    .fillMaxWidth()  // This will make the button span both columns
                                    .height(100.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEC6C5B),    // Background color
                                    contentColor = Color.White      // Text color
                                )
                            ) {
                                Text("Letters", color = Color.White, style = CustomTypography.bodyLarge.copy(fontSize = 24.sp))
                            }
                        }
                    }
                }
            }
        }
    )
}
    }

