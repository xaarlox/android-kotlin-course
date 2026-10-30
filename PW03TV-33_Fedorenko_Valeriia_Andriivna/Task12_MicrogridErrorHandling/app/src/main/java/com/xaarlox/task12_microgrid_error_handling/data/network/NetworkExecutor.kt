package com.xaarlox.task12_microgrid_error_handling.data.network

import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogEntry
import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogger
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

class RetryInfo(val nextAttempt: Int, val maxAttempts: Int, val delayMs: Long)

/**
 * CENTRALIZED ERROR HANDLER: all API calls go through execute().
 * For each attempt, it:
 *   1) checks connectivity;
 *   2) executes the request (Retrofit + Gson inside block);
 *   3) maps any exception to AppError and LOGS every failed attempt;
 *   4) decides whether to retry (only transient errors and only idempotent requests).
 * Exceptions are not thrown outward: an ApiResult is returned, and the ViewModel decides what to display
 *
 * idempotent = false (state-modifying POST): auto-retry is disabled because retrying after a timeout
 * could execute the action twice; the user will press "Retry" manually instead
 */
class NetworkExecutor(
    private val connectivity: ConnectivityChecker,
    private val logger: ErrorLogger,
    private val policy: RetryPolicy = RetryPolicy()
) {
    suspend fun <T> execute(
        operation: String,
        idempotent: Boolean = true,
        onRetry: (RetryInfo) -> Unit = {},
        block: suspend () -> T
    ): ApiResult<T> {
        var attempt = 1
        while (true) {
            if (!connectivity.isOnline()) {
                logger.log(
                    ErrorLogEntry(
                        System.currentTimeMillis(), operation, AppError.NoConnection.code,
                        null, "Пристрій офлайн", attempt, false
                    )
                )
                return ApiResult.Failure(AppError.NoConnection, attempt)
            }
            try {
                return ApiResult.Success(block(), attempt)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val error = ErrorMapper.map(e)
                val willRetry = idempotent && error.isTransient && attempt < policy.maxAttempts
                logger.log(
                    ErrorLogEntry(
                        time = System.currentTimeMillis(),
                        operation = operation,
                        code = error.code,
                        httpStatus = (error as? AppError.Http)?.status,
                        message = "${e.javaClass.simpleName}: ${e.message ?: "—"}",
                        attempt = attempt,
                        willRetry = willRetry
                    )
                )
                if (!willRetry) return ApiResult.Failure(error, attempt)

                val pause = policy.delayFor(attempt, error)
                onRetry(RetryInfo(attempt + 1, policy.maxAttempts, pause))
                delay(pause.milliseconds)
                attempt++
            }
        }
    }
}