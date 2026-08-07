package com.example.kanalogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanalogin.ui.theme.CustomTypography

@Composable
fun ProgressBarRepresentation(
    currentEXP: String?,
    nextEXP: String?
) {
    // Convert currentEXP and nextEXP to integers, or default to 0 if they're null
    val currentExpValue = currentEXP?.toIntOrNull() ?: 0
    val nextExpValue = nextEXP?.toIntOrNull() ?: 0

    // Calculate the progress ratio, avoid division by zero
    val progress = if (nextExpValue > 0) currentExpValue.toFloat() / nextExpValue.toFloat() else 0f

    // Display the progress bar
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Level up progress",
            style = CustomTypography.bodyLarge.copy(fontSize = 14.sp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = Color.Green,
        )
    }
}