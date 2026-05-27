package com.example.uniroomies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.uniroomies.di.AppDependencies
import com.example.uniroomies.domain.model.UserProfile
import com.example.uniroomies.domain.usecase.RegisterUserUseCase
import com.example.uniroomies.domain.usecase.SignInUseCase
import com.example.uniroomies.domain.usecase.SignOutUseCase

class AuthViewModel(
    private val signInUseCase: SignInUseCase = AppDependencies.signInUseCase,
    private val registerUserUseCase: RegisterUserUseCase = AppDependencies.registerUserUseCase,
    private val signOutUseCase: SignOutUseCase = AppDependencies.signOutUseCase
) : ViewModel() {
    fun signOut() {
        signOutUseCase()
    }

    fun signIn(
        email: String,
        password: String,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        signInUseCase(email, password, onResult)
    }

    fun register(
        email: String,
        password: String,
        name: String,
        age: String,
        sex: String,
        city: String,
        university: String,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        val profile = UserProfile(
            name = name,
            age = age,
            sex = sex,
            city = city,
            university = university,
            email = email
        )
        registerUserUseCase(email, password, profile, onResult)
    }
}
