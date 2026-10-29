package com.xaarlox.task11_microgrid_jwt_auth.domain

enum class StationType(val label: String) {
    SOLAR("Сонячна"), WIND("Вітрова"), STORAGE("Накопичувач");

    companion object {
        fun fromCode(code: String?): StationType? =
            entries.firstOrNull { it.name.equals(code, ignoreCase = true) }
    }
}

data class Station(
    val id: Int,
    val name: String,
    val type: StationType,
    val powerKw: Double,
    val online: Boolean
)

data class AppUser(val username: String, val role: Role)