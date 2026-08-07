package com.example.kanalogin.menu

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanalogin.ui.theme.CustomTypography
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController, context: Context) {
    val subject = remember { mutableStateOf("") }
    val feedbackText = remember { mutableStateOf("") }
    val isFeedbackSent = remember { mutableStateOf(false) } // State for feedback sent status
    val errorMessage = remember { mutableStateOf("") } // State for error messages

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Feedback",
                        style = CustomTypography.bodyLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "We value your feedback!",
                    style = CustomTypography.bodyLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Subject field
                TextField(
                    value = subject.value,
                    onValueChange = { subject.value = it },
                    label = { Text("Subject") },
                    placeholder = { Text("Enter the subject of your feedback...") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Feedback text field
                TextField(
                    value = feedbackText.value,
                    onValueChange = { feedbackText.value = it },
                    label = { Text("Your feedback") },
                    placeholder = { Text("Type your feedback here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.White),
                )

                // Display error message if any
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = Color.Red)
                }

                // Send feedback button
                Button(
                    onClick = {
                        // Clear previous error messages
                        errorMessage.value = ""

                        // Validate input
                        if (subject.value.isEmpty() || feedbackText.value.isEmpty()) {
                            errorMessage.value = "Both subject and feedback are required."
                            return@Button
                        }

                        // Send feedback to Firestore
                        sendFeedbackToFirestore(
                            subject.value,
                            feedbackText.value,
                            onSuccess = {
                                isFeedbackSent.value = true
                                // Remove navigation to menu screen
                                // Optionally, show a success message instead
                            },
                            onFailure = {
                                errorMessage.value = "Failed to send feedback. Please try again later."
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        text = "Send Feedback",
                        color = Color.White,
                        style = CustomTypography.bodyLarge.copy(fontSize = 16.sp)
                    )
                }

                // Show success message
                if (isFeedbackSent.value) {
                    Text(
                        text = "Feedback sent successfully!",
                        color = Color.Green,
                        style = CustomTypography.bodyLarge.copy(fontSize = 18.sp)
                    )
                }
            }
        }
    )
}

// Function to send feedback to Firestore
fun sendFeedbackToFirestore(
    subject: String,
    feedbackText: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    // Create a map for the feedback data to be stored in Firestore
    val feedbackData = hashMapOf<String, Any>(
        "subject" to subject,
        "feedback" to feedbackText,
        "timestamp" to System.currentTimeMillis(),
        "user" to (FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous")
    )

    // Send data to Firestore
    firestore.collection("feedbacks")
        .add(feedbackData) // Add the feedback to the Firestore collection
        .addOnSuccessListener {
            // Successfully added feedback to Firestore
            onSuccess() // Do not navigate, just show success message
        }
        .addOnFailureListener { exception ->
            // Handle failure case
            onFailure()
        }
}
