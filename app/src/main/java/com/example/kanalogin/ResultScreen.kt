package com.example.kanalogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kanalogin.ui.theme.CustomTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    navController: NavController,
    correctCount: Int,
    wrongCount: Int,
    totalEXP: Int,
    userLevel: Long
) {
    val leveledUpString = navController.currentBackStackEntry?.arguments?.getString("leveledUp")
    val leveledUp = leveledUpString?.toBoolean() ?: false
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Results",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = CustomTypography.bodyLarge.copy(fontSize = 24.sp)
                    )
                }
            )
        },
        content = { paddingValues ->  // Using paddingValues here to apply padding to the content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)  // Applied Scaffold content padding here
                    .padding(16.dp),  // Extra padding inside the column
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Correct Answers: $correctCount", style = CustomTypography.bodyLarge.copy(fontSize = 22.sp))
                Text("Wrong Answers: $wrongCount", style = CustomTypography.bodyLarge.copy(fontSize = 22.sp))
                Text("Total EXP Collected: $totalEXP", style = CustomTypography.bodyLarge.copy(fontSize = 22.sp))


                if (leveledUp) {
                    Text("Congratulations! You leveled up to Level $userLevel!",
                        color = Color(0xFF4CAF50), style = CustomTypography.bodyLarge.copy(fontSize = 25.sp)
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack("menu", false) }) {
                    Text("Go Back to Menu", color = Color.White, style = CustomTypography.bodyLarge.copy(fontSize = 22.sp))
                }
            }
        }
    )
}
