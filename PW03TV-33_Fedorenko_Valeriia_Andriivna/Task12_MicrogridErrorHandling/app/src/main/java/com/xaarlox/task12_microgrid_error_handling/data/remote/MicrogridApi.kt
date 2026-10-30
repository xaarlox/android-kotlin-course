package com.xaarlox.task12_microgrid_error_handling.data.remote

import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.AcceptedDto
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.AnalyticsBatch
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.StationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit converts non-2xx responses into HttpException, and network issues into IOException;
 * Gson throws JsonParseException on malformed JSON. All of this is caught by NetworkExecutor
 */
interface MicrogridApi {
    @GET("api/stations")
    suspend fun getStations(): List<StationDto>

    @POST("api/stations/{id}/toggle")
    suspend fun toggleStation(@Path("id") id: Int): StationDto

    @POST("analytics/errors")
    suspend fun sendErrors(@Body batch: AnalyticsBatch): AcceptedDto
}