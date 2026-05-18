package com.login.repository.response

data class LoginResponse(
    val success: Boolean?,
    val token: String?,
    val user: User?
)

data class User(
    val name: String?
)

data class UserResponse(
    val id: Int?,
    val name: String?,
    val username: String?,
    val email: String?
)
