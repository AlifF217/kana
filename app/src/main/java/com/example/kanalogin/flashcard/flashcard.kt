package com.example.kanalogin.flashcard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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

data class Flashcard(val imageRes: Int, val correctAnswer: String, val options: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Flashcard(navController: NavController, currentUser: FirebaseUser?) {
    val totalQuestions = 10
    var currentQuestionIndex by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var tempEXP by remember { mutableIntStateOf(0) }  // Track temporary EXP
    var leveledUp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) } // To show exit confirmation overlay

    var currentCard by remember { mutableStateOf(generateRandomCard()) }
    var feedbackMessage by remember { mutableStateOf("") }
    var showFeedback by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    var userLevel by remember { mutableLongStateOf(1) }
    var userCurrentEXP by remember { mutableLongStateOf(0) }
    var userNextEXP by remember { mutableLongStateOf(100) }

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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Flashcard",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { showExitConfirmation = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                        }
                    )
                    LinearProgressIndicator(
                        progress = { currentQuestionIndex / totalQuestions.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        color = Color.Green,
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "What is this?",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = CustomTypography.bodyLarge.copy(fontSize = 22.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = currentCard.imageRes),
                        contentDescription = "Object Image",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OptionsGrid(currentCard.options) { selectedAnswer ->
                        if (selectedAnswer == currentCard.correctAnswer) {
                            feedbackMessage = "Correct!"
                            correctCount++
                            tempEXP += 10  // Accumulate temporary EXP
                        } else {
                            feedbackMessage = "Wrong!"
                            wrongCount++
                        }
                        showFeedback = true
                        coroutineScope.launch {
                            delay(1500) // Show feedback for 1.5 seconds
                            showFeedback = false
                            if (currentQuestionIndex == totalQuestions) {
                                // After the last question, add the temporary EXP to the user
                                userCurrentEXP += tempEXP
                                addExperience(
                                    tempEXP,
                                    db,
                                    currentUser
                                ) { newLevel, newEXP, newNextEXP ->
                                    if (newLevel > userLevel) leveledUp = true
                                    userLevel = newLevel
                                        userCurrentEXP = newEXP
                                        userNextEXP = newNextEXP


                                }

                                navController.navigate("results/$correctCount/$wrongCount/$tempEXP/$leveledUp/$userLevel")
                            } else {

                                currentQuestionIndex++
                                currentCard = generateRandomCard()
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FeedbackOverlay(feedbackMessage)
        }


            AnimatedVisibility(
                visible = showExitConfirmation,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExitConfirmationDialog(
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

@Composable
fun OptionsGrid(options: List<String>, onOptionSelected: (String) -> Unit) {
    Column {
        options.chunked(2).forEach { rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                rowOptions.forEach { option ->
                    Button(
                        onClick = { onOptionSelected(option) },
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
                            text = option,
                            style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)
                        )
                    }
                }
            }
        }
    }
}

