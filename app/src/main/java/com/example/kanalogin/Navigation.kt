@file:Suppress("NAME_SHADOWING")

package com.example.kanalogin

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kanalogin.buildasentence.SentenceGameScreen
import com.example.kanalogin.flashcard.Flashcard
import com.example.kanalogin.letters.Letters
import com.example.kanalogin.menu.*
import com.example.kanalogin.menu.accountsettings.ChangeEmailScreen
import com.example.kanalogin.menu.accountsettings.ChangePasswordScreen
import com.example.kanalogin.menu.accountsettings.ChangeUsernameScreen
import com.example.kanalogin.menu.accountsettings.DeleteAccountScreen
import com.example.kanalogin.storytime.StoryTime
import com.example.kanalogin.menu.userprofile.ProfilePictureScreen
import com.example.kanalogin.sayit.SayIt
import com.example.kanalogin.sayit.SayitTraining
import com.example.kanalogin.sayit.Sayit_Course
import com.example.kanalogin.ui.theme.KanaLoginTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainApp(onLogout: () -> Unit) {

    MenuScreen(onLogout = onLogout, navController = rememberNavController())

    val navController = rememberNavController()
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val currentUser = FirebaseAuth.getInstance().currentUser

    // State variables for user details
    var userName by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }
    var userLevel by remember { mutableStateOf<Long?>(null) }
    var userRank by remember { mutableStateOf<String?>(null) }
    var userStreak by remember { mutableStateOf<Long?>(null) }
    var userTrophies by remember { mutableStateOf<Long?>(null) }
    var userCurrentExp by remember { mutableStateOf<Long?>(null) }
    var userNextExp by remember { mutableStateOf<Long?>(null) }

    // Theme state management
    var isDarkMode by remember { mutableStateOf(false) }
    val onThemeChanged: (Boolean) -> Unit = { isChecked ->
        isDarkMode = isChecked
    }

    // Fetch user data from Firestore
    fun fetchUserData() {
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userName = document.getString("username")
                    userEmail = document.getString("email")
                    userLevel = document.getLong("level")
                    userRank = document.getString("rank")
                    userStreak = document.getLong("streak")
                    userTrophies = document.getLong("trophies")
                    userCurrentExp = document.getLong("currentExp")
                    userNextExp = document.getLong("requiredExp")
                }
            }
        }
    }


    KanaLoginTheme(darkTheme = isDarkMode) {
        NavHost(navController = navController, startDestination = if (isLoggedIn) "menu" else "login") {

            composable("login") {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = { navController.navigate("register") },
                    onNavigateToMenu = {
                        fetchUserData()
                        navController.navigate("menu")
                    }
                )
            }

            composable("register") {
                val registerViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToLogin = { navController.popBackStack("login", inclusive = false) }
                )
            }

            composable("menu") {
                LaunchedEffect(navController.currentBackStackEntry) {
                    fetchUserData() // Refresh data when returning to the menu
                }
                // Pass onLogout function to MenuScreen
                MenuScreen(onLogout = {
                    FirebaseAuth.getInstance().signOut() // Sign out the user
                    navController.navigate("login") // Navigate back to the login screen
                }, navController = navController)
            }

            composable("settings") {

                SettingsScreen(

                        isDarkMode = isDarkMode,
                        onThemeChanged = onThemeChanged,
                    navController = navController
                )
            }

            composable(
                "change_email_screen"

            ) {

                // Call the ChangeEmailScreen with arguments
                ChangeEmailScreen(
                    navController = navController)
            }


            composable("feedback") {
                FeedbackScreen()
            }

            composable("progressSharing/{userName}/{userEmail}") { backStackEntry ->
                val context = LocalContext.current
                val userName = backStackEntry.arguments?.getString("userName")
                val userEmail = backStackEntry.arguments?.getString("userEmail")

                ProgressSharingScreen(
                    navController = navController,
                    userName = userName,
                    userEmail = userEmail,
                    context = context

                )
            }

            composable("letters") {
                Letters(navController = navController) }

            composable("flashcard") { val currentUser = FirebaseAuth.getInstance().currentUser
                Flashcard(navController = navController, currentUser = currentUser) }


            composable("buildasentence") { val currentUser = FirebaseAuth.getInstance().currentUser
                SentenceGameScreen(navController = navController, currentUser = currentUser) }

            composable("sayit") { SayIt(navController = navController,
                isDarkMode = isDarkMode, onThemeChanged = onThemeChanged) }

            composable("storytime") {  val currentUser = FirebaseAuth.getInstance().currentUser
                StoryTime(navController = navController,currentUser = currentUser) }
            composable("profilePicture") { ProfilePictureScreen(navController = navController) }

            composable("sayit_training") {
                SayitTraining(navController = navController) }// Navigates to the Training screen
            composable("sayit_basic_course") {  val currentUser = FirebaseAuth.getInstance().currentUser
                Sayit_Course(navController = navController,currentUser = currentUser) }// Navigates to the Training screen

            //result screen
            composable("results/{correctCount}/{wrongCount}/{totalEXP}/{leveledUp}/{userLevel}") { backStackEntry ->
                val correctCount = backStackEntry.arguments?.getString("correctCount")?.toInt() ?: 0
                val wrongCount = backStackEntry.arguments?.getString("wrongCount")?.toInt() ?: 0
                val totalEXP = backStackEntry.arguments?.getString("totalEXP")?.toInt() ?: 0
                val userLevel = backStackEntry.arguments?.getString("userLevel")?.toLong() ?: 1

                ResultsScreen(
                    navController = navController,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    totalEXP = totalEXP,
                    userLevel = userLevel
                )


            }


            //account stuff
            composable("change_username_screen") {


                ChangeUsernameScreen(navController = navController)
            }
            composable("change_email_screen") {


                    ChangeEmailScreen(navController = navController)

            }
            composable("change_password_screen") {
                ChangePasswordScreen(navController = navController)
            }
            composable("delete_account_screen") {
                DeleteAccountScreen(navController = navController)
            }

        }
    }
}
