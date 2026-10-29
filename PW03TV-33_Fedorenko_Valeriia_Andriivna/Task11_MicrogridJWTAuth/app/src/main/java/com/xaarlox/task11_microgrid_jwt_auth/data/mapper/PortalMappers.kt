package com.xaarlox.task11_microgrid_jwt_auth.data.mapper

import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.StationDto
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.dto.UserDto
import com.xaarlox.task11_microgrid_jwt_auth.domain.AppUser
import com.xaarlox.task11_microgrid_jwt_auth.domain.Role
import com.xaarlox.task11_microgrid_jwt_auth.domain.Station
import com.xaarlox.task11_microgrid_jwt_auth.domain.StationType

/** Invalid elements are discarded (null): better to skip an entry than to crash */
fun StationDto.toDomain(): Station? {
    val stationId = id ?: return null
    val stationName = name ?: return null
    val stationType = StationType.fromCode(type) ?: return null
    return Station(stationId, stationName, stationType, powerKw ?: 0.0, online ?: false)
}

fun UserDto.toDomain(): AppUser? {
    val userName = username ?: return null
    val userRole = Role.fromClaim(role) ?: return null
    return AppUser(userName, userRole)
}