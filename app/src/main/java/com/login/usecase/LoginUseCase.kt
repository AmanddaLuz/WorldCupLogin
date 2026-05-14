package com.login.usecase

import android.content.Context
import com.login.model.LoginModel
import com.login.repository.LoginRepository

class LoginUseCase(private val repository: LoginRepository) {

    suspend fun login(context: Context, user: String, password: String): Result<LoginModel> {
        val response = repository.login(context, user, password)
        return response
    }
}