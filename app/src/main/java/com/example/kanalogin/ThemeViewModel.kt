package com.example.kanalogin

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    // Theme state
    var isDarkMode by mutableStateOf(false)
        private set

    // Toggle the theme
    fun toggleTheme() {
        isDarkMode = !isDarkMode
    }
}