package com.example.kanalogin.letters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.ui.theme.CustomTypography
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Letters(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {
        showExitConfirmation = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kana",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    text = { Text("Hiragana", color = MaterialTheme.colorScheme.onBackground, style = CustomTypography.bodyLarge.copy(fontSize = 20.sp)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                Tab(
                    text = { Text("Katakana", color = MaterialTheme.colorScheme.onBackground, style = CustomTypography.bodyLarge.copy(fontSize = 20.sp)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                Tab(
                    text = { Text("Kanji", color = MaterialTheme.colorScheme.onBackground, style = CustomTypography.bodyLarge.copy(fontSize = 20.sp)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Let's learn ${getTabTitle(selectedTab)}!",
                color = MaterialTheme.colorScheme.onBackground,
                style = CustomTypography.bodyLarge.copy(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            val characters = getCharacterList(selectedTab)
            val buttonColors = remember { mutableStateMapOf<String, Color>().apply { characters.forEach { this[it] = Color(0xFFF17175) } } }

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(characters) { character ->
                    var isClicked by remember { mutableStateOf(false) }
                    val animatedColor by animateColorAsState(targetValue = buttonColors[character] ?: Color(0xFFF17175), label = "")

                    // Handle button click logic
                    Button(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = animatedColor,
                            contentColor = Color.White
                        ),
                        onClick = {
                            // Change button color to darker shade on click
                            buttonColors[character] = Color(0xFFB00020)
                            isClicked = true
                            playSound(context, character)
                        }
                    ) {
                        Text(character, color = Color.White, style = CustomTypography.bodyLarge.copy(fontSize = 24.sp))
                    }

                    // Handle color reset and state after button click
                    if (isClicked) {
                        LaunchedEffect(character) {
                            delay(800.milliseconds)  // Wait for 800ms before resetting color
                            buttonColors[character] = Color(0xFFF17175)  // Revert to original color
                            isClicked = false
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun getTabTitle(tabIndex: Int): String {
    return when (tabIndex) {
        0 -> "Hiragana"
        1 -> "Katakana"
        else -> "Kanji"
    }
}

fun getCharacterList(tabIndex: Int): List<String> {
    return when (tabIndex) {
        0 -> listOf(
            "あ", "い", "う", "え", "お",
            "か", "き", "く", "け", "こ",
            "さ", "し", "す", "せ", "そ",
            "た", "ち", "つ", "て", "と",
            "な", "に", "ぬ", "ね", "の"
        )
        1 -> listOf(
            "ア", "イ", "ウ", "エ", "オ",
            "カ", "キ", "ク", "ケ", "コ",
            "サ", "シ", "ス", "セ", "ソ",
            "タ", "チ", "ツ", "テ", "ト",
            "ナ", "ニ", "ヌ", "ネ", "ノ"
        )
        else -> listOf(
            "日", "月", "火", "水", "木",
            "金", "土", "空", "天", "地",
            "人", "山", "川", "森", "花"
        )
    }
}

@SuppressLint("DiscouragedApi")
fun playSound(context: Context, character: String) {
    val validResourceName = when (character) {
        "あ", "ア" -> "a"
        "い", "イ" -> "i"
        "う", "ウ" -> "u"
        "え", "エ" -> "e"
        "お", "オ" -> "o"
        "か", "カ" -> "ka"
        "き", "キ" -> "ki"
        "く", "ク" -> "ku"
        "け", "ケ" -> "ke"
        "こ", "コ" -> "ko"
        "さ", "サ" -> "sa"
        "し", "シ" -> "shi"
        "す", "ス" -> "su"
        "せ", "セ" -> "se"
        "そ", "ソ" -> "so"
        "た", "タ" -> "ta"
        "ち", "チ" -> "chi"
        "つ", "ツ" -> "tsu"
        "て", "テ" -> "te"
        "と", "ト" -> "to"
        "な", "ナ" -> "na"
        "に", "ニ" -> "ni"
        "ぬ", "ヌ" -> "nu"
        "ね", "ネ" -> "ne"
        "の", "ノ" -> "no"
        "日" -> "hi"
        "月" -> "tsuki"
        "火" -> "hi"
        "水" -> "mizu"
        "木" -> "ki"
        "金" -> "kane"
        "土" -> "do"
        "空" -> "sora"
        "天" -> "ten"
        "地" -> "chi"
        "人" -> "hito"
        "山" -> "yama"
        "川" -> "kawa"
        "森" -> "mori"
        "花" -> "hana"
        else -> null
    }

    validResourceName?.let { resourceName ->
        val soundResourceId = context.resources.getIdentifier(
            resourceName,
            "raw",
            context.packageName
        )

        if (soundResourceId != 0) {
            val mediaPlayer = MediaPlayer.create(context, soundResourceId)
            mediaPlayer.start()
        } else {
            println("Sound resource not found for character: $character")
        }
    }
}
