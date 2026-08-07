package com.example.kanalogin.buildasentence

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.ExitConfirmationDialog
import com.example.kanalogin.addExperience
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.kanalogin.ui.theme.CustomTypography
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SentenceGameScreen(navController: NavController, currentUser: FirebaseUser?) {
    val totalQuestions = 10
    var currentQuestionIndex by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var tempEXP by remember { mutableIntStateOf(0) }  // Track temporary EXP
    var leveledUp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) } // To show exit confirmation overlay

    var userLevel by remember { mutableLongStateOf(1) }
    var userCurrentEXP by remember { mutableLongStateOf(0) }
    var userNextEXP by remember { mutableLongStateOf(100) }

    var feedbackMessage by remember { mutableStateOf("") }
    var showFeedback by remember { mutableStateOf(false) }
    val userSelection = remember { mutableStateListOf<String>() }
    var questionIndex by remember { mutableIntStateOf(Random.nextInt(sentencePairs.size)) }
    var currentQuestionPair by remember { mutableStateOf(sentencePairs[questionIndex]) }
    var englishWords by remember {
        mutableStateOf(
            currentQuestionPair.second.split(" ").shuffled()
        )
    }

    val englishWordsVisibility = remember { mutableStateMapOf<String, Boolean>() }

    val (japaneseQuestion, englishAnswer) = currentQuestionPair
    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

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

    LaunchedEffect(englishWords) {
        englishWords.forEach { word ->
            englishWordsVisibility[word] = true
        }
    }

// Handle back button press
    BackHandler(enabled = true) {
        showExitConfirmation = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Build-a-Sentence",
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
                    // Progress bar for question navigation
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
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Question text
                    Text(
                        text = "Translate and rearrange the sentence:",
                        style = CustomTypography.bodyLarge.copy(fontSize = 34.sp),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "\n$japaneseQuestion",
                        color = Color.Green,
                        style = CustomTypography.bodyLarge.copy(fontSize = 34.sp),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )

                    // User's sentence assembly box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        userSelection.forEach { word ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                                    .clickable {
                                        userSelection.remove(word)
                                        englishWordsVisibility[word] = true // Show the word again in the selection list
                                    }
                            ) {
                                Text(text = word, color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }

                    // English words to select from
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        englishWords.forEach { word ->
                            AnimatedVisibility(visible = englishWordsVisibility[word] == true) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFF17175),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (!userSelection.contains(word)) {
                                                userSelection.add(word)
                                                englishWordsVisibility[word] = false // Hide the word in the selection list

                                            }
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(text = word, color = Color.White, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    // Submit Button
                    Button(
                        onClick = {
                            val userAnswer = userSelection.joinToString(" ")
                            if (userAnswer == englishAnswer) {
                                feedbackMessage = "Correct!"
                                correctCount++
                                tempEXP += 10 // Accumulate temporary EXP
                            } else {
                                feedbackMessage = "Wrong!"
                                wrongCount++
                            }

                            showFeedback = true
                            userSelection.clear()

                            coroutineScope.launch {
                                delay(1500.milliseconds) // Show feedback for 1.5 seconds
                                showFeedback = false
                                if (currentQuestionIndex == totalQuestions) {
                                    // Add EXP after completing the quiz
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
                                    navController.navigate(
                                        "results/$correctCount/$wrongCount/$tempEXP/$leveledUp/$userLevel"
                                    )
                                } else {
                                    currentQuestionIndex++
                                    questionIndex = Random.nextInt(sentencePairs.size)
                                    currentQuestionPair = sentencePairs[questionIndex]
                                    englishWords = currentQuestionPair.second.split(" ").shuffled()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Submit", style = CustomTypography.bodyLarge)
                    }
                }
            }
        )

        // Feedback overlay
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


