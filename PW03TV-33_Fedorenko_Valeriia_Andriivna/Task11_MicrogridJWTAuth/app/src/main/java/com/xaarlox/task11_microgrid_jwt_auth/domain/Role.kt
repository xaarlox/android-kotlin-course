package com.xaarlox.task11_microgrid_jwt_auth.domain

enum class Role(val label: String) {
    ADMIN("Адміністратор"),
    USER("Користувач");

    companion object {
        fun fromClaim(claim: String?): Role? =
            entries.firstOrNull { it.name.equals(claim, ignoreCase = true) }
    }
}