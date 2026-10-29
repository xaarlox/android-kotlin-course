package com.xaarlox.task10_microgrid_rest_api.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * We store RAW meteorological data (radiation, wind) in the DB, not the calculated power:
 * formulas and station parameters can be changed, and the data won't need to be re-fetched
 */
@Entity(tableName = "forecast_days")
data class ForecastEntity(
    @PrimaryKey val date: String,
    val radiationMj: Double,
    val windMaxMs: Double,
    val location: String
)