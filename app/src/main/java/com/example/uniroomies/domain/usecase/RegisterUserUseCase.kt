package com.example.uniroomies.domain.usecase

import com.example.uniroomies.domain.model.UserProfile
import com.example.uniroomies.domain.repository.AuthRepository

class RegisterUserUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        profile: UserProfile,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        repository.register(email, password, profile, onResult)
    }
}
