package com.xaarlox.task11_microgrid_jwt_auth.data.mock

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.JwtPayload
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.ErrorDto
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.LoginRequest
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.StationDto
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.TokenResponse
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.UserDto
import com.xaarlox.task11_microgrid_jwt_auth.domain.Role
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

/**
 * Mock REST server with authorization. This is a terminal Interceptor: no network request is sent.
 * The server, just like a real one, ITSELF validates the signature, expiration, and role:
 *   401: token is missing, corrupted, or expired;
 *   403: token is valid, but the role is insufficient
 */
class MockBackendInterceptor(
    private val tokenTtlSeconds: Long = 120,
    private val latencyMs: Long = 500
) : Interceptor {

    private data class Account(val username: String, val password: String, val role: Role)

    private val gson = Gson()
    private val accounts = listOf(
        Account("admin", "admin123", Role.ADMIN),
        Account("user", "user123", Role.USER)
    )
    private val stations = mutableListOf(
        StationDto(1, "СЕС «Північна»", "SOLAR", 120.0, true),
        StationDto(2, "СЕС «Південна»", "SOLAR", 95.5, true),
        StationDto(3, "Вітропарк «Захід»", "WIND", 210.0, true),
        StationDto(4, "Акумуляторна станція", "STORAGE", 60.0, false)
    )
    private val toggleRoute = Regex("^/api/stations/(\\d+)/toggle$")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Thread.sleep(latencyMs)
        val path = request.url.encodedPath
        val method = request.method

        return when {
            method == "POST" && path == "/auth/login" -> login(request)
            method == "GET" && path == "/api/stations" ->
                secured(request, required = null) {
                    json(
                        request,
                        200,
                        synchronized(stations) { stations.toList() })
                }

            method == "GET" && path == "/api/admin/users" ->
                secured(request, Role.ADMIN) {
                    json(request, 200, accounts.map { UserDto(it.username, it.role.name) })
                }

            method == "POST" && toggleRoute.matches(path) ->
                secured(request, Role.ADMIN) {
                    toggle(request, toggleRoute.matchEntire(path)!!.groupValues[1].toInt())
                }

            else -> errorResponse(request, 404, "Ресурс не знайдено")
        }
    }

    private fun login(request: Request): Response {
        val buffer = Buffer().also { request.body?.writeTo(it) }
        val credentials = try {
            gson.fromJson(buffer.readUtf8(), LoginRequest::class.java)
        } catch (e: JsonParseException) {
            null
        }
        val account = accounts.firstOrNull {
            it.username == credentials?.username && it.password == credentials.password
        } ?: return errorResponse(request, 401, "Невірний логін або пароль")

        val now = System.currentTimeMillis() / 1000
        val token = MockJwt.create(
            JwtPayload(
                sub = account.username,
                role = account.role.name,
                iat = now,
                exp = now + tokenTtlSeconds
            ),
            SECRET
        )
        return json(request, 200, TokenResponse(token))
    }

    /** Shared access check for all protected resources */
    private fun secured(request: Request, required: Role?, onGranted: () -> Response): Response {
        val header = request.header("Authorization")
            ?: return errorResponse(request, 401, "Потрібна автентифікація: токен відсутній")
        if (!header.startsWith("Bearer ")) {
            return errorResponse(request, 401, "Некоректний заголовок Authorization")
        }
        val payload = when (val check = MockJwt.verify(
            header.removePrefix("Bearer ").trim(),
            SECRET,
            System.currentTimeMillis() / 1000
        )) {
            is TokenCheck.Valid -> check.payload
            TokenCheck.Expired -> return errorResponse(request, 401, "Термін дії токена минув")
            TokenCheck.Invalid -> return errorResponse(request, 401, "Токен недійсний")
        }
        if (required != null && Role.fromClaim(payload.role) != required) {
            return errorResponse(
                request,
                403,
                "Недостатньо прав: потрібна роль «${required.label}»"
            )
        }
        return onGranted()
    }

    private fun toggle(request: Request, id: Int): Response = synchronized(stations) {
        val index = stations.indexOfFirst { it.id == id }
        if (index < 0) return@synchronized errorResponse(request, 404, "Станцію не знайдено")
        val updated = stations[index].let { it.copy(online = !(it.online ?: false)) }
        stations[index] = updated
        json(request, 200, updated)
    }

    private fun json(request: Request, code: Int, body: Any): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(gson.toJson(body).toResponseBody("application/json".toMediaType()))
            .build()

    private fun errorResponse(request: Request, code: Int, message: String) =
        json(request, code, ErrorDto(message))

    private companion object {
        const val SECRET = "mock-secret-for-lab"
    }
}