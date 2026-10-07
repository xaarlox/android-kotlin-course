package com.xaarlox.task05_microgridnavapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.xaarlox.task05_microgridnavapp.data.UserProfile

/**
 * Shared ViewModel containing the user state
 * Single source of truth for the profile: survives screen rotation,
 * and the UI automatically updates when [profile] changes
 */
class UserViewModel : ViewModel() {
    /** Current profile; can only be modified via [updateProfile] */
    var profile by mutableStateOf(UserProfile())
        private set

    /** Saves new profile data (an empty name is replaced with the default value) */
    fun updateProfile(name: String, email: String) {
        profile = UserProfile(
            name = name.trim().ifEmpty { UserProfile.DEFAULT_NAME },
            email = email.trim()
        )
    }
}