package com.login.repository

import android.content.Context
import com.login.model.LoginModel
import com.login.repository.response.LoginResponse

interface LoginRepository {

    suspend fun login(email: String, password: String): Result<LoginModel>
}
