package com.xaarlox.task11_microgrid_jwt_auth

import android.app.Application

/** The dependency container lives as long as the process, so there is a single SessionManager for the entire application */
class MicrogridApp : Application() {
    val container by lazy { AppContainer(this) }
}