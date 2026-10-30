package com.xaarlox.task12_microgrid_error_handling.data.remote.mock

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.AcceptedDto
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.AnalyticsBatch
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.ErrorDto
import com.xaarlox.task12_microgrid_error_handling.data.remote.dto.StationDto
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.random.Random

/**
 * Server simulation that behaves according to the selected FaultScenario.
 * Network failures are modeled with exceptions (as in real OkHttp), and HTTP errors with responses
 * containing the corresponding code. The analytics endpoint is exempt from simulated failures
 */
class MockBackendInterceptor(
    private val simulator: FaultSimulator,
    private val latencyMs: Long = 400,
    private val timeoutDelayMs: Long = 1_200
) : Interceptor {

    private val gson = Gson()
    private val stations = mutableListOf(
        StationDto(1, "СЕС «Північна»", "SOLAR", 120.0, true),
        StationDto(2, "СЕС «Південна»", "SOLAR", 95.5, true),
        StationDto(3, "Вітропарк «Захід»", "WIND", 210.0, true),
        StationDto(4, "Акумуляторна станція", "STORAGE", 60.0, false)
    )
    private val toggleRoute = Regex("^/api/stations/(\\d+)/toggle$")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        Thread.sleep(latencyMs)
        if (chain.call().isCanceled()) throw IOException("Canceled")

        if (path == "/analytics/errors") return analytics(request)

        when (simulator.scenario.value) {
            FaultScenario.NORMAL -> Unit
            FaultScenario.OFFLINE -> throw UnknownHostException("Unable to resolve host")
            FaultScenario.TIMEOUT -> {
                Thread.sleep(timeoutDelayMs)
                throw SocketTimeoutException("timeout")
            }

            FaultScenario.FLAKY ->
                if (simulator.consumeFlakyFailure()) throw ConnectException("Connection reset by peer")

            FaultScenario.SERVER_500 -> return errorResponse(request, 500, "Внутрішня помилка")
            FaultScenario.SERVER_503 ->
                return errorResponse(request, 503, "Сервіс недоступний", "Retry-After" to "1")

            FaultScenario.RATE_LIMIT ->
                return errorResponse(request, 429, "Забагато запитів", "Retry-After" to "2")

            FaultScenario.NOT_FOUND -> return errorResponse(request, 404, "Не знайдено")
            FaultScenario.UNAUTHORIZED -> return errorResponse(request, 401, "Токен недійсний")
            FaultScenario.FORBIDDEN -> return errorResponse(request, 403, "Заборонено")
            FaultScenario.BAD_JSON -> return raw(request, 200, """{"stations": [oops""")
        }

        return when {
            request.method == "GET" && path == "/api/stations" -> synchronized(stations) {
                json(request, 200, stations.map { it.withJitter() })
            }

            request.method == "POST" && toggleRoute.matches(path) ->
                toggle(request, toggleRoute.matchEntire(path)!!.groupValues[1].toInt())

            else -> errorResponse(request, 404, "Ресурс не знайдено")
        }
    }

    private fun toggle(request: Request, id: Int): Response = synchronized(stations) {
        val index = stations.indexOfFirst { it.id == id }
        if (index < 0) return@synchronized errorResponse(request, 404, "Станцію не знайдено")
        val updated = stations[index].let { it.copy(online = !(it.online ?: false)) }
        stations[index] = updated
        json(request, 200, updated)
    }

    private fun analytics(request: Request): Response {
        val buffer = Buffer().also { request.body?.writeTo(it) }
        val count = try {
            gson.fromJson(buffer.readUtf8(), AnalyticsBatch::class.java)?.errors?.size
        } catch (e: JsonParseException) {
            null
        } ?: 0
        return json(request, 202, AcceptedDto(count))
    }

    /** Slight power fluctuations to make the update noticeable */
    private fun StationDto.withJitter(): StationDto =
        if (online == true) copy(powerKw = powerKw?.times(Random.nextDouble(0.95, 1.05))) else this

    private fun json(request: Request, code: Int, body: Any, vararg headers: Pair<String, String>) =
        raw(request, code, gson.toJson(body), *headers)

    private fun errorResponse(
        request: Request,
        code: Int,
        message: String,
        vararg headers: Pair<String, String>
    ) = json(request, code, ErrorDto(message), *headers)

    private fun raw(
        request: Request,
        code: Int,
        body: String,
        vararg headers: Pair<String, String>
    ): Response {
        val builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(statusText(code))
            .body(body.toResponseBody("application/json".toMediaType()))
        headers.forEach { (name, value) -> builder.header(name, value) }
        return builder.build()
    }

    private fun statusText(code: Int) = when (code) {
        200 -> "OK"
        202 -> "Accepted"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        429 -> "Too Many Requests"
        500 -> "Internal Server Error"
        503 -> "Service Unavailable"
        else -> "Status $code"
    }
}