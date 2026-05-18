package com.login.usecase

import android.content.Context
import com.login.model.LoginModel
import com.login.repository.LoginRepository

class LoginUseCase(private val repository: LoginRepository) {

    suspend fun login(user: String, password: String): Result<LoginModel> {
        val response = repository.login(user, password)
        return response
    }
}
