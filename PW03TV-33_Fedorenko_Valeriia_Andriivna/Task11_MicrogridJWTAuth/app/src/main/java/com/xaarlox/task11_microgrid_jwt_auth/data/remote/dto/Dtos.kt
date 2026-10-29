package com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val token: String?)
data class ErrorDto(val message: String?)

data class StationDto(
    val id: Int?,
    val name: String?,
    val type: String?,
    val powerKw: Double?,
    val online: Boolean?
)

data class UserDto(val username: String?, val role: String?)