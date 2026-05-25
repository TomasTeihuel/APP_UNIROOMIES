package com.example.uniroomies.data.repository

import com.example.uniroomies.data.model.UserProfileDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun signOut() {
        auth.signOut()
    }

    fun signIn(
        email: String,
        password: String,
        onResult: (Result<UserProfileDto>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid.orEmpty()
                loadProfile(uid, email, onResult)
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }

    fun register(
        email: String,
        password: String,
        profile: UserProfileDto,
        onResult: (Result<UserProfileDto>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid.orEmpty()
                val profileWithAuth = profile.copy(uid = uid, email = email)
                onResult(Result.success(profileWithAuth))

                firestore.collection("profiles")
                    .document(uid)
                    .set(profileWithAuth)
                    .addOnFailureListener { error ->
                        error.printStackTrace()
                    }
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }

    private fun loadProfile(
        uid: String,
        email: String,
        onResult: (Result<UserProfileDto>) -> Unit
    ) {
        if (uid.isBlank()) {
            onResult(Result.failure(IllegalStateException("No se pudo leer el usuario autenticado.")))
            return
        }

        firestore.collection("profiles")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(UserProfileDto::class.java)
                    ?: UserProfileDto(uid = uid, email = email, name = "Estudiante UNIROOMIES")
                onResult(Result.success(profile))
            }
            .addOnFailureListener { error ->
                onResult(Result.failure(error))
            }
    }
}
