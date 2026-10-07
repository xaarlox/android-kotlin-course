package com.xaarlox.task05_microgridnavapp.domain

import java.util.Locale

/**
 * Business logic for calculating the solar power plant (SPP) capacity
 */
object PowerCalculator {
    const val POWER_PER_PANEL_KW = 0.3 // Nominal power of a single panel, kW
    const val MAX_PANELS_DIGITS = 6 // Maximum allowed number of panels in the form

    fun calculate(panels: Int): Double = panels * POWER_PER_PANEL_KW

    /** Formats the value with one decimal place using the Ukrainian locale (9,9) */
    fun format(powerKw: Double): String =
        String.format(Locale.forLanguageTag("uk-UA"), "%.1f", powerKw)
}