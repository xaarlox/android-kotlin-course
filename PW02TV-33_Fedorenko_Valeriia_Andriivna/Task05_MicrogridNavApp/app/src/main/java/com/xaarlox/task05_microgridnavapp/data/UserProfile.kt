package com.xaarlox.task05_microgridnavapp.data

/**
 * User profile model (saved settings)
 *
 * @property name user's name
 * @property email email address (can be empty)
 */
data class UserProfile(
    val name: String = DEFAULT_NAME,
    val email: String = ""
) {
    companion object {
        const val DEFAULT_NAME = "Anonymous"
    }
}