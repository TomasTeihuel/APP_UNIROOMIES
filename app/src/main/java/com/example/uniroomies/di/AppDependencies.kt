package com.example.uniroomies.di

import com.example.uniroomies.data.remote.datasource.AuthRemoteDataSource
import com.example.uniroomies.data.repository.AuthRepositoryImpl
import com.example.uniroomies.domain.repository.AuthRepository
import com.example.uniroomies.domain.usecase.RegisterUserUseCase
import com.example.uniroomies.domain.usecase.SignInUseCase
import com.example.uniroomies.domain.usecase.SignOutUseCase

object AppDependencies {
    private val authRemoteDataSource = AuthRemoteDataSource()
    private val authRepository: AuthRepository = AuthRepositoryImpl(authRemoteDataSource)

    val signInUseCase = SignInUseCase(authRepository)
    val registerUserUseCase = RegisterUserUseCase(authRepository)
    val signOutUseCase = SignOutUseCase(authRepository)
}
