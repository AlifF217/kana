@file:Suppress("DEPRECATION")

package com.example.kanalogin.menu.accountsettings

import android.widget.Toast
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context

// This is the standard visual transformation for password fields
val passwordVisualTransformation = PasswordVisualTransformation()

fun changeUsername(user: FirebaseUser, newUsername: String) {
    // Update Firebase Authentication display name
    val profileUpdates = UserProfileChangeRequest.Builder()
        .setDisplayName(newUsername)
        .build()

    user.updateProfile(profileUpdates).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Successfully updated display name in Firebase Auth
            updateUsernameInFirestore(user.uid, newUsername)
        } else {
            // Handle the error here
            println("Error updating username in Firebase Auth: ${task.exception?.message}")
        }
    }
}

fun updateUsernameInFirestore(userId: String, newUsername: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.update("username", newUsername)
        .addOnSuccessListener {
            println("Username successfully updated in Firestore.")
        }
        .addOnFailureListener { e ->
            println("Error updating username in Firestore: ${e.message}")
        }
}




fun changeUserEmail(
    context: Context,
    currentEmail: String,
    currentPassword: String,
    newEmail: String,
    onSuccess: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Toast.makeText(context, "User is not signed in", Toast.LENGTH_SHORT).show()
        return
    }

    val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)

    // Re-authenticate user
    user.reauthenticate(credential)
        .addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Update email
                user.updateEmail(newEmail)
                    .addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            // Update Firestore if necessary
                            val db = FirebaseFirestore.getInstance()
                            val userRef = db.collection("users").document(user.uid)
                            userRef.update("email", newEmail)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Email updated successfully", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Failed to update email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Re-authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}







fun changePassword(user: FirebaseUser, newPassword: String) {
    user.updatePassword(newPassword).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Successfully updated password in Firebase Auth
            println("Password successfully updated.")
        } else {
            // Handle the error here
            println("Error updating password: ${task.exception?.message}")
        }
    }
}

fun deleteAccount(user: FirebaseUser, navController: NavController) {
    user.delete().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Successfully deleted account
            println("Account successfully deleted.")
            // Clear the navigation stack and navigate to the login screen
            navController.navigate("login") {
                // Clear all destinations in the back stack
                popUpTo("login") { inclusive = true }
                launchSingleTop = true // Ensures that only one instance of the login screen is in the back stack
            }
        } else {
            // Handle the error here
            println("Error deleting account: ${task.exception?.message}")
        }
    }
}