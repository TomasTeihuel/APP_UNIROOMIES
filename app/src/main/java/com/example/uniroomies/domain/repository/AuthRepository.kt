package com.example.uniroomies.domain.repository

import com.example.uniroomies.domain.model.UserProfile

interface AuthRepository {
    fun signIn(
        email: String,
        password: String,
        onResult: (Result<UserProfile>) -> Unit
    )

    fun register(
        email: String,
        password: String,
        profile: UserProfile,
        onResult: (Result<UserProfile>) -> Unit
    )

    fun signOut()
}
