package com.example.uniroomies.domain.usecase

import com.example.uniroomies.domain.repository.AuthRepository

class SignOutUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}
