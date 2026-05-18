package com.login.network

import com.login.repository.response.UserResponse
import retrofit2.http.GET

interface LoginApi {

    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}