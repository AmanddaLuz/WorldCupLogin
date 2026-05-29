package com.login.domain.usecase

import com.login.data.model.LoginModel
import com.login.domain.repository.LoginRepository

class LoginUseCase(private val repository: LoginRepository) {

    suspend fun login(user: String, password: String): Result<LoginModel> {
        val response = repository.login(user, password)
        return response
    }
}
