package com.login.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private const val BASE_URL =
        "https://jsonplaceholder.typicode.com/"

    fun create(): LoginApi {

        val logging =
            HttpLoggingInterceptor().apply {
                HttpLoggingInterceptor.Level.BODY

            }

        val client =
            OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(LoginApi::class.java)
    }
}
