package com.xaarlox.task11_microgrid_jwt_auth.data.auth

import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.util.Base64

/**
 * Token claims (exp, iat in seconds, as per the JWT standard).
 * Fields are nullable: Gson ignores Kotlin's null-safety, so we validate manually
 */
data class JwtPayload(
    val sub: String?,
    val role: String?,
    val iat: Long?,
    val exp: Long?
)

/**
 * The client only READS the payload to know the role and expiration time. The client cannot
 * verify the signature (the secret is server-side only), so the role from the token is used
 * ONLY for UI display. Actual access control is enforced by the server (403)
 */
object JwtDecoder {
    private val gson = Gson()

    fun decode(token: String): JwtPayload? = try {
        val parts = token.split(".")
        if (parts.size != 3) {
            null
        } else {
            val json = String(Base64.getUrlDecoder().decode(parts[1]), Charsets.UTF_8)
            gson.fromJson(json, JwtPayload::class.java)
        }
    } catch (e: IllegalArgumentException) {
        null
    } catch (e: JsonParseException) {
        null
    }
}