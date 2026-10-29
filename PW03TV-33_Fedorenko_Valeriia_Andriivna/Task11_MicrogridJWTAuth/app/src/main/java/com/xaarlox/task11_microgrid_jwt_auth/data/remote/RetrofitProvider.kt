package com.xaarlox.task11_microgrid_jwt_auth.data.remote

import com.xaarlox.task11_microgrid_jwt_auth.data.auth.SessionManager
import com.xaarlox.task11_microgrid_jwt_auth.data.mock.MockBackendInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    fun create(sessionManager: SessionManager): MicrogridApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(MockBackendInterceptor(tokenTtlSeconds = 120))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://mock.microgrid.local/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicrogridApi::class.java)
    }
}