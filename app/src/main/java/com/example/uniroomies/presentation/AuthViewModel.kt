package com.example.uniroomies.presentation

import androidx.lifecycle.ViewModel
import com.example.uniroomies.data.model.UserProfileDto
import com.example.uniroomies.data.repository.AuthRepository

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    fun signOut() {
        repository.signOut()
    }

    fun signIn(
        email: String,
        password: String,
        onResult: (Result<UserProfileDto>) -> Unit
    ) {
        repository.signIn(email, password, onResult)
    }

    fun register(
        email: String,
        password: String,
        name: String,
        age: String,
        sex: String,
        city: String,
        university: String,
        onResult: (Result<UserProfileDto>) -> Unit
    ) {
        val profile = UserProfileDto(
            name = name,
            age = age,
            sex = sex,
            city = city,
            university = university,
            email = email
        )
        repository.register(email, password, profile, onResult)
    }
}
