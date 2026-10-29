package com.xaarlox.task11_microgrid_jwt_auth.util

import kotlin.coroutines.cancellation.CancellationException

/** Like runCatching, but does not swallow coroutine cancellation */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }