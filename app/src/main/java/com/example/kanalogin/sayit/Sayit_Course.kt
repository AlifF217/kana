package com.example.kanalogin.sayit

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.ExitConfirmationDialog
import com.example.kanalogin.addExperience
import com.example.kanalogin.ui.theme.CustomTypography
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sayit_Course(navController: NavController, currentUser: FirebaseUser?) {
    val context = LocalContext.current
    val voiceToTextParser = remember { VoiceToTextParser(context) }

    var currentPhrase by remember { mutableStateOf(randomPhrases.random()) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var canRecord by remember { mutableStateOf(false) }
    var processedText by remember { mutableStateOf("") }
    var isWrongAnswer by remember { mutableStateOf(false) }

    var showExitConfirmation by remember { mutableStateOf(false) } // To show exit confirmation overlay

    val totalQuestions = 10
    var currentQuestionIndex by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }

    var tempEXP by remember { mutableIntStateOf(0) }  // Track temporary EXP
    var userLevel by remember { mutableLongStateOf(1) }
    var userCurrentEXP by remember { mutableLongStateOf(0) }
    var userNextEXP by remember { mutableLongStateOf(100) }
    var leveledUp by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    val coroutineScope = rememberCoroutineScope()

    // For record permission request
    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> canRecord = isGranted }
    )

    LaunchedEffect(Unit) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

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

    val state by voiceToTextParser.state.collectAsState()

    // Utility function to convert romanized text to Katakana
    fun convertToKatakana(input: String): String {
        val romajiToKatakanaMap = mapOf(
            "a" to "ア", "i" to "イ", "u" to "ウ", "e" to "エ", "o" to "オ",
            "ka" to "カ", "ki" to "キ", "ku" to "ク", "ke" to "ケ", "ko" to "コ",
            "sa" to "サ", "shi" to "シ", "su" to "ス", "se" to "セ", "so" to "ソ",
            "ta" to "タ", "chi" to "チ", "tsu" to "ツ", "te" to "テ", "to" to "ト",
            "na" to "ナ", "ni" to "ニ", "nu" to "ヌ", "ne" to "ネ", "no" to "ノ",
            "ha" to "ハ", "hi" to "ヒ", "fu" to "フ", "he" to "ヘ", "ho" to "ホ",
            "ma" to "マ", "mi" to "ミ", "mu" to "ム", "me" to "メ", "mo" to "モ",
            "ya" to "ヤ", "yu" to "ユ", "yo" to "ヨ",
            "ra" to "ラ", "ri" to "リ", "ru" to "ル", "re" to "レ", "ro" to "ロ",
            "wa" to "ワ", "wo" to "ヲ", "n" to "ン",
            "ga" to "ガ", "gi" to "ギ", "gu" to "グ", "ge" to "ゲ", "go" to "ゴ",
            "za" to "ザ", "ji" to "ジ", "zu" to "ズ", "ze" to "ゼ", "zo" to "ゾ",
            "da" to "ダ", "ji" to "ヂ", "zu" to "ヅ", "de" to "デ", "do" to "ド",
            "ba" to "バ", "bi" to "ビ", "bu" to "ブ", "be" to "ベ", "bo" to "ボ",
            "pa" to "パ", "pi" to "ピ", "pu" to "プ", "pe" to "ペ", "po" to "ポ",
            "kya" to "キャ", "kyu" to "キュ", "kyo" to "キョ",
            "sha" to "シャ", "shu" to "シュ", "sho" to "ショ",
            "cha" to "チャ", "chu" to "チュ", "cho" to "チョ",
            "nya" to "ニャ", "nyu" to "ニュ", "nyo" to "ニョ",
            "hya" to "ヒャ", "hyu" to "ヒュ", "hyo" to "ヒョ",
            "mya" to "ミャ", "myu" to "ミュ", "myo" to "ミョ",
            "rya" to "リャ", "ryu" to "リュ", "ryo" to "リョ",
            "gya" to "ギャ", "gyu" to "ギュ", "gyo" to "ギョ",
            "ja" to "ジャ", "ju" to "ジュ", "jo" to "ジョ",
            "bya" to "ビャ", "byu" to "ビュ", "byo" to "ビョ",
            "pya" to "ピャ", "pyu" to "ピュ", "pyo" to "ピョ",
            "wi" to "ウィ", "we" to "ウェ", "wo" to "ヲ",
            "vu" to "ヴ", "va" to "ヴァ", "vi" to "ヴィ", "ve" to "ヴェ", "vo" to "ヴォ"
        )

        return input.lowercase().split(" ").joinToString("") { word ->
            word.map { romajiToKatakanaMap[it.toString()] ?: it.toString() }.joinToString("")
        }
    }

    // Process user input for conversion
    LaunchedEffect(key1 = state.spokenText) {
        if (state.spokenText.isNotEmpty()) {
            // Remove spaces between words and combine them into one string
            val processedInput = state.spokenText.replace(" ", "").lowercase()

            processedText = if (processedInput.any { it.isLowerCase() }) {
                processedInput.uppercase()
            } else {
                convertToKatakana(processedInput)
            }

            if (processedText == currentPhrase) {
                feedbackMessage = "Correct!"
                showFeedback = true
                correctCount++
                tempEXP += 10
                currentQuestionIndex++

                delay(2000) // Show feedback for 2 seconds
                showFeedback = false
                if (currentQuestionIndex <= totalQuestions) {
                    currentPhrase = randomPhrases.random()
                    isWrongAnswer = false
                }
            } else {
                feedbackMessage = "Wrong!"
                showFeedback = true
                delay(2000) // Show feedback for 2 seconds
                showFeedback = false
                isWrongAnswer = true
                wrongCount++
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                "Training",
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (state.isSpeaking) {
                            voiceToTextParser.stopListening()
                        } else {
                            voiceToTextParser.startListening("ja-JP") // Start listening in Japanese
                        }
                    }
                ) {
                    AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking ->
                        if (isSpeaking) {
                            Icon(
                                imageVector = Icons.Rounded.Stop,
                                contentDescription = "Stop Listening"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Mic,
                                contentDescription = "Start Listening"
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instruction text for using the mic

                    Text(
                        text = "Wait 1 second before speaking into the mic.",
                        modifier = Modifier.padding(bottom = 30.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )



                Text(
                    text = "Say this: \n $currentPhrase",
                    modifier = Modifier.padding(bottom = 30.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking ->
                    if (isSpeaking) {
                        Text(
                            text = "Listening...",
                            modifier = Modifier.padding(bottom = 20.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = processedText.ifEmpty { "Touch the mic to record." },
                                modifier = Modifier.padding(bottom = 20.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Change Sentence button only visible if the answer was wrong
                if (isWrongAnswer) {
                    Button(
                        onClick = {
                            currentPhrase = randomPhrases.random()
                            showFeedback = false // Hide feedback message if visible
                            isWrongAnswer = false // Reset wrong answer state
                        },
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(text = "Change Sentence")
                    }
                }
            }
        }
// Handle back button press
        BackHandler(enabled = true) {
            showExitConfirmation = true
        }

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
    LaunchedEffect(currentQuestionIndex) {
        if (currentQuestionIndex == totalQuestions) {
            userCurrentEXP += tempEXP
            addExperience(tempEXP, db, currentUser) { newLevel, newEXP, newNextEXP ->
                if (newLevel > userLevel)
                    leveledUp = false
                userLevel = newLevel
                userCurrentEXP = newEXP
                userNextEXP = newNextEXP
            }

            navController.navigate("results/$correctCount/$wrongCount/$tempEXP/$leveledUp/$userLevel")
        }
    }
}
