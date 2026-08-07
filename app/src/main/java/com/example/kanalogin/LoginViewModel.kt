package com.example.kanalogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun signIn(onSuccess: () -> Unit) {
        val emailValue = email.value.trim()
        val passwordValue = password.value.trim()

        // Check for empty fields
        if (emailValue.isEmpty() && passwordValue.isEmpty()) {
            setErrorMessage("Fields cannot be empty.")
            return
        }

        else if (emailValue.isEmpty()) {
            setErrorMessage("Email field cannot be empty.")
            return
        }

        else if (passwordValue.isEmpty()) {
            setErrorMessage("Password field cannot be empty.")
            return
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login success
                        setErrorMessage(null) // Clear any previous error message
                        onSuccess()
                    } else {
                        // Handle login failure
                        val error = task.exception?.message ?: "Wrong email or password."
                        setErrorMessage(error)
                    }
                }
        }
    }
}
