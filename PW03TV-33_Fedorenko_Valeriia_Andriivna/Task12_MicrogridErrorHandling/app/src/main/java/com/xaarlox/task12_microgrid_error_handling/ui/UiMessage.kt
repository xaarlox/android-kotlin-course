package com.xaarlox.task12_microgrid_error_handling.ui

/** One-time message for a Snackbar. If retry is specified, a "Retry" button is shown */
data class UiMessage(val text: String, val retry: (() -> Unit)? = null)