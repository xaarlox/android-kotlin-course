package com.xaarlox.task11_microgrid_jwt_auth.data.mock

import com.google.gson.Gson
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.JwtDecoder
import com.xaarlox.task11_microgrid_jwt_auth.data.auth.JwtPayload
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

sealed interface TokenCheck {
    class Valid(val payload: JwtPayload) : TokenCheck
    object Invalid : TokenCheck
    object Expired : TokenCheck
}

/** Server-side JWT (HS256): issuance and signature verification. The client does not use this code */
object MockJwt {
    private val gson = Gson()
    private val encoder = Base64.getUrlEncoder().withoutPadding()

    fun create(payload: JwtPayload, secret: String): String {
        val header = encoder.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray())
        val body = encoder.encodeToString(gson.toJson(payload).toByteArray())
        return "$header.$body.${sign("$header.$body", secret)}"
    }

    fun verify(token: String, secret: String, nowSeconds: Long): TokenCheck {
        val parts = token.split(".")
        if (parts.size != 3) return TokenCheck.Invalid

        val expected = sign("${parts[0]}.${parts[1]}", secret)
        if (!MessageDigest.isEqual(expected.toByteArray(), parts[2].toByteArray())) {
            return TokenCheck.Invalid
        }
        val payload = JwtDecoder.decode(token) ?: return TokenCheck.Invalid
        val exp = payload.exp ?: return TokenCheck.Invalid
        return if (nowSeconds >= exp) TokenCheck.Expired else TokenCheck.Valid(payload)
    }

    private fun sign(data: String, secret: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(), "HmacSHA256"))
        return encoder.encodeToString(mac.doFinal(data.toByteArray()))
    }
}