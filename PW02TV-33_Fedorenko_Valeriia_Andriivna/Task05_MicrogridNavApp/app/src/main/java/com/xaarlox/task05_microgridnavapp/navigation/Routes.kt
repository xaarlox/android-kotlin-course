package com.xaarlox.task05_microgridnavapp.navigation

object Routes {
    const val MAIN_MENU = "main_menu"
    const val USER_INFO = "user_info"
    const val FORECAST_INPUT = "forecast_input"
    const val ARG_PANELS = "panels"
    private const val FORECAST_DETAILS = "forecast_details"

    /** Route template for NavHost */
    const val FORECAST_DETAILS_ROUTE = "$FORECAST_DETAILS/{$ARG_PANELS}"

    /** Builds the details screen route with the provided number of panels */
    fun forecastDetails(panels: Int) = "$FORECAST_DETAILS/$panels"
}