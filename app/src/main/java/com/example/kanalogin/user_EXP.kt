package com.example.kanalogin

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

fun addExperience(
    gainedEXP: Int,
    db: FirebaseFirestore,
    currentUser: FirebaseUser?,
    onLevelUp: (Long, Long, Long) -> Unit
) {
    currentUser?.let { user ->
        val userRef = db.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentEXP = document.getLong("currentExp") ?: 0
                val nextEXP = document.getLong("requiredExp") ?: 100
                val level = document.getLong("level") ?: 1

                val newEXP = currentEXP + gainedEXP
                if (newEXP >= nextEXP) {
                    // Level up
                    val newLevel = level + 1
                    val newNextEXP = (nextEXP * 1.5).toLong() // Increase EXP threshold
                    userRef.update(
                        mapOf(
                            "level" to newLevel,
                            "currentExp" to 0,
                            "requiredExp" to newNextEXP
                        )
                    )
                    onLevelUp(newLevel, 0, newNextEXP)
                } else {
                    userRef.update("currentExp", newEXP)
                    onLevelUp(level, newEXP, nextEXP)
                }
            }
        }
    }
}
