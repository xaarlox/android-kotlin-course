package com.xaarlox.task12_microgrid_error_handling.data.remote

import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.FaultSimulator
import com.xaarlox.task12_microgrid_error_handling.data.remote.mock.MockBackendInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    fun create(simulator: FaultSimulator): MicrogridApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(MockBackendInterceptor(simulator))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://mock.microgrid.local/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicrogridApi::class.java)
    }
}