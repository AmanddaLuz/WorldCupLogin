package com.login.repository.response

data class LoginResponse(
    val success: Boolean?,
    val token: String?,
    val user: User?
)

data class User(
    val name: String?
)