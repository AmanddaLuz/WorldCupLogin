package com.login.repository.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean?,
    val token: String?,
    val user: User?
)

data class User(
    val name: String?
)

data class UserResponse(

    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("email")
    val email: String?
)
