package com.login.data.model

data class LoginModel(
    val success: Boolean,
    val token: String,
    val user: User
)

data class User(
    val name: String
)
