package com.xaarlox.task12_microgrid_error_handling.data.network

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/** The single place where technical exceptions are converted into AppError */
object ErrorMapper {

    fun map(t: Throwable): AppError = when (t) {
        is HttpException -> AppError.Http(t.code(), retryAfterMs(t))
        is SocketTimeoutException -> AppError.Timeout
        is IOException -> AppError.Network
        is JsonParseException, is InvalidDataException -> AppError.InvalidData
        else -> AppError.Unknown
    }

    private fun retryAfterMs(e: HttpException): Long? =
        e.response()?.headers()?.get("Retry-After")?.trim()?.toLongOrNull()?.times(1000)
}