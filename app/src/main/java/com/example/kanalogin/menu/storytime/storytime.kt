package com.example.kanalogin.menu.storytime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.flashcard.generateRandomCard
import com.example.kanalogin.ui.theme.CustomTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryTime(navController: NavController) {
    var message by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }

    var currentIndex by remember { mutableIntStateOf(Random.nextInt(StoriesData.getStories().size)) }
    val stories = StoriesData.getStories()
    val coroutineScope = rememberCoroutineScope()

    fun showRandomQuestion() {
        currentIndex = Random.nextInt(stories.size)
    }

    val (story, questionData) = stories[currentIndex]

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Story Time",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)) },
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
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (row in questionData.answers.chunked(2)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (answer in row) {
                                    Button(
                                        onClick = {
                                            message =
                                                if (answer == questionData.correctAnswer) "Correct!" else "Wrong!"
                                            showMessage = true
                                            coroutineScope.launch {
                                                delay(1500) // Show feedback for 1.5 seconds
                                                showMessage = false
                                                showRandomQuestion()
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
    }
}

@Composable
fun FeedbackOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
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

