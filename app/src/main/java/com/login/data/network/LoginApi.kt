package com.login.data.network

import com.login.domain.repository.response.UserResponse
import retrofit2.http.GET

interface LoginApi {

    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}
