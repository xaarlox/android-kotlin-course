package com.xaarlox.task11_microgrid_jwt_auth.data.remote

import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.LoginRequest
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.StationDto
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.TokenResponse
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit defines the API, Gson parses JSON. The Authorization header is NOT passed here:
 * it is injected by AuthInterceptor, so no call can "forget" the token.
 * The exception is the diagnostic methods at the bottom, which intentionally need to send a request without a token
 */
interface MicrogridApi {

    @Headers("X-Skip-Auth: true")
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @GET("api/stations")
    suspend fun getStations(): List<StationDto>


    @POST("api/stations/{id}/toggle")
    suspend fun toggleStation(@Path("id") id: Int): StationDto

    @GET("api/admin/users")
    suspend fun getUsers(): List<UserDto>

    @Headers("X-Skip-Auth: true")
    @GET("api/stations")
    suspend fun getStationsAnonymous(): List<StationDto>

    @GET("api/stations")
    suspend fun getStationsWithToken(@Header("Authorization") authorization: String): List<StationDto>
}