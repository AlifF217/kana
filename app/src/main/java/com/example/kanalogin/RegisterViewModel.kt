package com.example.kanalogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _username = MutableStateFlow("")  // Added username state
    val username: StateFlow<String> = _username

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onUsernameChange(newUsername: String) {  // Added username change function
        _username.value = newUsername
    }

    private fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    // Register method remains the same
    fun register(onSuccess: () -> Unit) {
        val emailValue = email.value.trim()
        val passwordValue = password.value.trim()
        val confirmPasswordValue = confirmPassword.value.trim()
        val usernameValue = username.value.trim()

        if (emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty() || usernameValue.isEmpty()) {
            setErrorMessage("All fields are required.")
            return
        }

        if (passwordValue != confirmPasswordValue) {
            setErrorMessage("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            saveUserDataToFirestore(it.uid, emailValue, usernameValue, null, onSuccess)
                        }
                    } else {
                        val error = task.exception?.message ?: "Registration failed."
                        setErrorMessage(error)
                    }
                }
        }
    }

    private fun saveUserDataToFirestore(userId: String, email: String, username: String, imageUrl: String?, onSuccess: () -> Unit) {
        val userData = hashMapOf(
            "email" to email,
            "username" to username,
            "userId" to userId,
            "profilePictureUrl" to (imageUrl ?: ""),
            "rank" to "Beginner",
            "level" to 1,
            "streak" to 0,
            "trophies" to 0,
            "currentExp" to 0,
            "requiredExp" to 100
        )

        firestore.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                setErrorMessage("Failed to save user data: ${e.message}")
            }
    }
}
