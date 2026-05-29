package com.login.domain.repository.response

import com.login.data.model.LoginModel
import com.login.data.model.User

fun LoginResponse.toDomain(): LoginModel {
    return LoginModel(
        success = success ?: false,
        token = token.orEmpty(),
        user = User(
            name = user?.name.orEmpty()
        )
    )
}
