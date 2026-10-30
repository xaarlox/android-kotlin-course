package com.xaarlox.task12_microgrid_error_handling.data.logging

import android.os.Build
import com.xaarlox.task12_microgrid_error_handling.data.network.ApiResult
import com.xaarlox.task12_microgrid_error_handling.data.network.NetworkExecutor
import com.xaarlox.task12_microgrid_error_handling.data.remote.MicrogridApi
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.AnalyticsBatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Error log + dispatching to the analytics server. Uploading also goes through NetworkExecutor,
 * so it benefits from connectivity checks and automatic retries
 */
class ErrorLogRepository(
    private val logger: ErrorLogger,
    private val api: MicrogridApi,
    private val executor: NetworkExecutor
) {
    val entries: Flow<List<ErrorLogEntry>> = logger.version.map { logger.readAll() }

    /** Success(n): n entries sent (the log is cleared afterwards) */
    suspend fun upload(): ApiResult<Int> {
        val items = logger.readAll()
        if (items.isEmpty()) return ApiResult.Success(0)

        val result = executor.execute("POST /analytics/errors") {
            api.sendErrors(AnalyticsBatch(deviceInfo(), items))
        }
        return when (result) {
            is ApiResult.Success -> {
                logger.clear()
                ApiResult.Success(items.size, result.attempts)
            }

            is ApiResult.Failure -> result
        }
    }

    suspend fun clear() = logger.clear()

    private fun deviceInfo() =
        "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
}