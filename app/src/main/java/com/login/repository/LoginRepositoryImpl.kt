package com.login.repository

import android.util.Log
import com.login.model.LoginModel
import com.login.model.User
import com.login.network.LoginApi


class LoginRepositoryImpl(
    private val api: LoginApi
) : LoginRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginModel> {

        return try {

            Log.d(
                "LOGIN_REQUEST",
                "email=$email password=$password"
            )

            val users = api.getUsers()

            Log.d(
                "LOGIN_RESPONSE",
                users.toString()
            )

            val user = users.firstOrNull {

                it.email.equals(
                    email,
                    ignoreCase = true
                ) &&

                        it.username.equals(
                            password,
                            ignoreCase = true
                        )
            }

            Log.d(
                "LOGIN_USER",
                user.toString()
            )

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

                Result.failure(
                    Throwable("Login inválido")
                )
            }

        } catch (exception: Exception) {

            Log.e(
                "LOGIN_ERROR",
                exception.message.orEmpty()
            )

            Result.failure(exception)
        }
    }
}