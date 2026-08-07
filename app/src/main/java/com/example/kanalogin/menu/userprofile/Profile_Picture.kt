@file:Suppress("DEPRECATION")

package com.example.kanalogin.menu.userprofile

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.R
import com.example.kanalogin.ui.theme.CustomTypography
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePictureScreen(navController: NavController) {
    val drawableNames = DrawableMappings.drawables.keys.toList() // Fetch names from DrawableMappings
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Profile Picture",
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {




                LazyColumn(modifier = Modifier.padding(paddingValues)) {
                    items(drawableNames) { drawableName ->
                        ProfilePictureItem(drawableName) {
                            updateProfilePicture(drawableName, context, currentUser)
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfilePictureItem(drawableName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = DrawableMappings.getDrawableId(drawableName)),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = drawableName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = CustomTypography.bodyLarge.copy(fontSize = 18.sp)
        )
    }
}

fun updateProfilePicture(drawableName: String, context: Context, currentUser: FirebaseUser?) {
    if (currentUser != null) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(currentUser.uid)

        userRef.update("userpfp", drawableName)
            .addOnSuccessListener {
                //Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                showCustomToast(context, "Profile picture updated")

            }
            .addOnFailureListener {
                //Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                showCustomToast(context, "Failed to update profile picture")

            }
    }
}

// Function to display the custom toast
fun showCustomToast(context: Context, message: String) {
    val inflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(R.layout.custom_toast, null)

    val toastIcon = layout.findViewById<ImageView>(R.id.toast_icon)
    val toastMessage = layout.findViewById<TextView>(R.id.toast_message)

    // Set the icon and message
    toastIcon.setImageResource(R.drawable.kana_logo_transparent) // Replace with your app logo resource
    toastMessage.text = message

    // Create and display the toast
    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
