package com.xaarlox.task12_microgrid_error_handling.data.network

/** Result of a network operation. attempts: how many attempts were needed (useful for UI and reporting) */
sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T, val attempts: Int = 1) : ApiResult<T>
    data class Failure(val error: AppError, val attempts: Int = 1) : ApiResult<Nothing>
}