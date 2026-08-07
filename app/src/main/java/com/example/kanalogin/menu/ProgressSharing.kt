@file:Suppress("DEPRECATION")

package com.example.kanalogin.menu

import android.R.drawable.ic_menu_camera
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.kanalogin.menu.userprofile.DrawableMappings.getDrawableId
import com.example.kanalogin.ui.theme.CustomTypography
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSharingScreen(
    userName: String?,
    userEmail: String?,
    context: Context, navController: NavController
) {
    var imageName by remember { mutableStateOf<String?>(null) }
    var userLevel by remember { mutableStateOf<Long?>(null) }
    var userRank by remember { mutableStateOf<String?>(null) }
    var userStreak by remember { mutableStateOf<Long?>(null) }
    var userTrophies by remember { mutableStateOf<Long?>(null) }
    var userCurrentEXP by remember { mutableStateOf<Long?>(null) }
    var userNextEXP by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(userName, userEmail) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .whereEqualTo("username", userName)
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                } else {
                    val userData = result.documents.first()
                    imageName = userData.getString("userpfp") // Fetch profile picture name
                    userLevel = userData.getLong("level")
                    userRank = userData.getString("rank")
                    userStreak = userData.getLong("streak")
                    userTrophies = userData.getLong("trophies")
                    userCurrentEXP = userData.getLong("currentExp")
                    userNextEXP = userData.getLong("requiredExp")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching user data: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Info
                Text("Username: ${userName ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Email: ${userEmail ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Level: ${userLevel ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Rank: ${userRank ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Streak: ${userStreak ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Trophies: ${userTrophies ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Current EXP: ${userCurrentEXP ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Next EXP: ${userNextEXP ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(32.dp))

                // Share Progress Button
                Button(
                    onClick = {
                        shareProgress(
                            context,
                            userName,
                            userLevel,
                            userRank,
                            userStreak,
                            userTrophies,
                            userCurrentEXP,
                            userNextEXP
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Share Progress", style = CustomTypography.bodyLarge.copy(color = Color.White))
                }
            }
        }
    )
}

fun shareProgress(
    context: Context,
    userName: String?,
    userLevel: Long?,
    userRank: String?,
    userStreak: Long?,
    userTrophies: Long?,
    userCurrentEXP: Long?,
    userNextEXP: Long?
) {
    val shareMessage = """
        Check out my progress in Kana!
        Username: $userName
        Level: ${userLevel ?: "N/A"}
        Rank: ${userRank ?: "N/A"}
        Streak: ${userStreak ?: "N/A"}
        Trophies: ${userTrophies ?: "N/A"}
        Current EXP: ${userCurrentEXP ?: "N/A"}
        Next EXP: ${userNextEXP ?: "N/A"}
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }

    try {
        startActivity(context, Intent.createChooser(sendIntent, "Share via"), null)
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing progress", Toast.LENGTH_SHORT).show()
    }
}
