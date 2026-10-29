package com.xaarlox.task11_microgrid_jwt_auth.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.ErrorDto
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class InvalidTokenException(message: String = "Сервер повернув некоректний токен") :
    Exception(message)

class InvalidResponseException : Exception("Сервер повернув некоректні дані")

/** Converts an exception into a user-friendly message; for HTTP errors, it takes the server message */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> serverMessage() ?: when (code()) {
        401 -> "Потрібна автентифікація"
        403 -> "Недостатньо прав"
        else -> "Помилка сервера (HTTP ${code()})"
    }

    is InvalidTokenException, is InvalidResponseException -> message.orEmpty()
    is UnknownHostException, is ConnectException -> "Немає підключення до інтернету"
    is SocketTimeoutException -> "Сервер не відповів вчасно"
    is JsonParseException -> "Не вдалося обробити відповідь сервера"
    is IOException -> "Помилка мережі"
    else -> message ?: "Невідома помилка"
}

private fun HttpException.serverMessage(): String? = try {
    response()?.errorBody()?.string()
        ?.let { Gson().fromJson(it, ErrorDto::class.java)?.message }
} catch (e: Exception) {
    null
}