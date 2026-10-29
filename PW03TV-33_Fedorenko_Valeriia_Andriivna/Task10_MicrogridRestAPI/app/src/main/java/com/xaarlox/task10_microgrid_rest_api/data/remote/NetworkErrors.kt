package com.xaarlox.task10_microgrid_rest_api.data.remote

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CityNotFoundException(city: String) : Exception("Місто «$city» не знайдено")
class InvalidResponseException : Exception("Сервер повернув некоректні дані")

/** Converts a technical exception into a user-friendly message */
fun Throwable.toUserMessage(): String = when (this) {
    is CityNotFoundException, is InvalidResponseException -> message.orEmpty()
    is HttpException -> "Помилка сервера (HTTP ${code()})"
    is UnknownHostException, is ConnectException -> "Немає підключення до інтернету"
    is SocketTimeoutException -> "Сервер не відповів вчасно"
    is JsonParseException -> "Не вдалося обробити відповідь сервера"
    is IOException -> "Помилка мережі"
    else -> message ?: "Невідома помилка"
}