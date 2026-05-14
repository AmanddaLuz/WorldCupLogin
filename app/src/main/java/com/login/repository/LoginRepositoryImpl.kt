package com.login.repository

import android.content.Context
import com.commons.JsonReader
import com.google.gson.Gson
import com.login.model.LoginModel
import com.login.repository.response.LoginResponse
import com.login.repository.response.toDomain
import kotlinx.coroutines.delay

class LoginRepositoryImpl: LoginRepository {
    override suspend fun login(
        context: Context,
        user: String,
        password: String
    ): Result<LoginModel> {

        delay(1500)

        return try {
            if (user.isEmpty() || password.isEmpty()){
                return Result.failure(Exception("Usuário ou senha inválidos"))
            } else {
                val json = JsonReader.readJsonFromAssets(context, "mock/login_success.json")
                val loginResponse = Gson().fromJson(json, LoginResponse::class.java)
                Result.success(loginResponse.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}