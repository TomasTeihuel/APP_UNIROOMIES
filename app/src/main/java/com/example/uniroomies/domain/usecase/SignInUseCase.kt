package com.example.uniroomies.domain.usecase

import com.example.uniroomies.domain.model.UserProfile
import com.example.uniroomies.domain.repository.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        repository.signIn(email, password, onResult)
    }
}
