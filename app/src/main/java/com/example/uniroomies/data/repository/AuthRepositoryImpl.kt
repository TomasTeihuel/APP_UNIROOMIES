package com.example.uniroomies.data.repository

import com.example.uniroomies.data.mapper.toDomain
import com.example.uniroomies.data.mapper.toDto
import com.example.uniroomies.data.remote.datasource.AuthRemoteDataSource
import com.example.uniroomies.domain.model.UserProfile
import com.example.uniroomies.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) : AuthRepository {
    override fun signIn(
        email: String,
        password: String,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        remoteDataSource.signIn(email, password) { result ->
            onResult(result.map { it.toDomain() })
        }
    }

    override fun register(
        email: String,
        password: String,
        profile: UserProfile,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        remoteDataSource.register(email, password, profile.toDto()) { result ->
            onResult(result.map { it.toDomain() })
        }
    }

    override fun signOut() {
        remoteDataSource.signOut()
    }
}
