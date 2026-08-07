package com.example.kanalogin.storytime


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.ExitConfirmationDialog
import com.example.kanalogin.addExperience
import com.example.kanalogin.ui.theme.CustomTypography
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryTime(navController: NavController, currentUser: FirebaseUser?) {
    val totalStories = StoriesData.getStories().size
    var currentStoryIndex by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var tempEXP by remember { mutableIntStateOf(0) }  // Track temporary EXP
    var leveledUp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) } // To show exit confirmation overlay

    var userLevel by remember { mutableLongStateOf(1) }
    var userCurrentEXP by remember { mutableLongStateOf(0) }
    var userNextEXP by remember { mutableLongStateOf(100) }

    var message by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    var currentIndex by remember { mutableIntStateOf(Random.nextInt(StoriesData.getStories().size)) }
    val stories = StoriesData.getStories()
    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    fun showRandomQuestion() {
        currentIndex = Random.nextInt(stories.size)
    }

    val (story, questionData) = stories[currentIndex]

    // Fetch initial user data
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userLevel = document.getLong("level") ?: 1
                    userCurrentEXP = document.getLong("currentExp") ?: 0
                    userNextEXP = document.getLong("requiredExp") ?: 100
                }
            }
        }
    }
// Handle back button press
    BackHandler(enabled = true) {
        showExitConfirmation = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                Column {
                TopAppBar(
                    title = {

                            Text(
                                text = "Story Time",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)
                            )},

                        navigationIcon = {
                            IconButton(onClick = { showExitConfirmation = true  }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                        }
                )
                            // Progress bar for navigation
                            LinearProgressIndicator(
                                progress = { currentStoryIndex / totalStories.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                color = Color.Green,
                            )
                        }

            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Story text
                    Text(
                        text = story,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = CustomTypography.bodyLarge.copy(fontSize = 20.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = questionData.question,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = CustomTypography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Answer options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in questionData.answers.chunked(2)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (answer in row) {
                                    Button(
                                        onClick = {
                                            if (answer == questionData.correctAnswer) {
                                                message = "Correct!"
                                                correctCount++
                                                tempEXP += 10 // Accumulate temporary EXP
                                            } else {
                                                message = "Wrong!"
                                                wrongCount++
                                            }

                                            showMessage = true

                                            coroutineScope.launch {
                                                delay(1500) // Show feedback for 1.5 seconds
                                                showMessage = false
                                                if (currentStoryIndex == totalStories) {

                                                    // Add EXP after completing the quiz
                                                    userCurrentEXP += tempEXP
                                                    addExperience(tempEXP, db, currentUser) { newLevel, newEXP, newNextEXP ->
                                                        if (newLevel > userLevel) leveledUp = true
                                                        userLevel = newLevel
                                                        userCurrentEXP = newEXP
                                                        userNextEXP = newNextEXP
                                                    }

                                                    navController.navigate(
                                                        "results/$correctCount/$wrongCount/$tempEXP/$leveledUp/$userLevel"
                                                    )
                                                } else {
                                                    currentStoryIndex++
                                                    showRandomQuestion()
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(200.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF17175),
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(
                                            text = answer,
                                            color = Color.White,
                                            style = CustomTypography.bodyLarge.copy(fontSize = 20.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        // Feedback overlay
        AnimatedVisibility(
            visible = showMessage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FeedbackOverlay(message)
        }

            AnimatedVisibility(
                visible = showExitConfirmation,
                enter = fadeIn(),
                exit = fadeOut()
            ){ExitConfirmationDialog(
                onConfirm = {
                    // Discard tempEXP and navigate back
                    tempEXP = 0
                    showExitConfirmation = false
                    navController.popBackStack() // Navigate back to the main page
                },
                onDismiss = { showExitConfirmation = false }
            )
        }
    }
}


// Feedback overlay
@Composable
fun FeedbackOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000))
            .pointerInput(Unit) {} // Consumes touch events, preventing interactions with elements behind
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                // Optionally consume clicks
            },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = if (message == "Correct!") Color(0xFF4CAF50) else Color(0xFFFF5722),
            modifier = Modifier.size(330.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 63.sp
                )
            }
        }
    }
}

