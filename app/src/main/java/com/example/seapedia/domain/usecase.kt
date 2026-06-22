package com.example.seapedia.domain

import com.example.seapedia.core.utils.Resource

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<User> =
        repository.login(email, password)
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        role: String
    ): Resource<User> = repository.register(name, email, password, role)
}
