package com.xaarlox.task11_microgrid_jwt_auth.data

import com.xaarlox.task11_microgrid_jwt_auth.data.mapper.toDomain
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.InvalidResponseException
import com.xaarlox.task11_microgrid_jwt_auth.data.remote.MicrogridApi
import com.xaarlox.task11_microgrid_jwt_auth.domain.AppUser
import com.xaarlox.task11_microgrid_jwt_auth.domain.Station
import com.xaarlox.task11_microgrid_jwt_auth.util.runCatchingCancellable

/** Protected resources. It knows nothing about tokens here: that is the job of AuthInterceptor */
class PortalRepository(private val api: MicrogridApi) {

    suspend fun stations(): Result<List<Station>> =
        runCatchingCancellable { api.getStations().mapNotNull { it.toDomain() } }

    suspend fun toggle(id: Int): Result<Station> = runCatchingCancellable {
        api.toggleStation(id).toDomain() ?: throw InvalidResponseException()
    }

    suspend fun users(): Result<List<AppUser>> =
        runCatchingCancellable { api.getUsers().mapNotNull { it.toDomain() } }

    suspend fun probeAnonymous() = runCatchingCancellable { api.getStationsAnonymous() }

    suspend fun probeBrokenToken() =
        runCatchingCancellable { api.getStationsWithToken("Bearer eyJhbGciOiJIUzI1NiJ9.broken.token") }

    suspend fun probeAdminResource() = runCatchingCancellable { api.getUsers() }
}