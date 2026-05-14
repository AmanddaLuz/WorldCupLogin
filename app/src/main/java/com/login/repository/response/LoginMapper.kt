package com.login.repository.response

import com.login.model.LoginModel
import com.login.model.User

fun LoginResponse.toDomain(): LoginModel {
    return LoginModel(
        success = success ?: false,
        token = token.orEmpty(),
        user = User(
            name = user?.name.orEmpty()
        )
    )
}
