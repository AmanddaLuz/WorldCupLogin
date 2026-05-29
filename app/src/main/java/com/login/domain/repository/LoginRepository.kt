package com.login.domain.repository

import com.login.data.model.LoginModel

interface LoginRepository {

    suspend fun login(email: String, password: String): Result<LoginModel>
}
