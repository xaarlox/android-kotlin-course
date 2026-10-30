package com.xaarlox.task12_microgrid_error_handling.data.remote.dto

import com.xaarlox.task12_microgrid_error_handling.data.logging.ErrorLogEntry

/** Nullable fields: Gson ignores Kotlin's null safety, so we validate them manually in the mapper */
data class StationDto(
    val id: Int?,
    val name: String?,
    val type: String?,
    val powerKw: Double?,
    val online: Boolean?
)

data class ErrorDto(val message: String?)
data class AnalyticsBatch(val device: String, val errors: List<ErrorLogEntry>)
data class AcceptedDto(val accepted: Int?)