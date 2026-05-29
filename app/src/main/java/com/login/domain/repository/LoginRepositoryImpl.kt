package com.login.domain.repository

import android.util.Log
import com.login.data.model.LoginModel
import com.login.data.model.User
import com.login.data.network.LoginApi

class LoginRepositoryImpl(
    private val api: LoginApi
) : LoginRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginModel> {

        return try {
            val users = api.getUsers()
            val user = users.firstOrNull {
                it.email.equals(email, ignoreCase = true) &&
                        it.username.equals(password, ignoreCase = true)
            }

            if (user != null) {
                Result.success(
                    LoginModel(
                        user = User(
                            name = user.name.orEmpty()
                        ),
                        success = true,
                        token = user.id.toString()
                    )
                )

            } else {

                Log.d("LoginRepositoryImpl", "$users")
                Result.failure(
                    Throwable("Login inválido")
                )
            }

        } catch (exception: Exception) {

            Result.failure(exception)
        }
    }
}
