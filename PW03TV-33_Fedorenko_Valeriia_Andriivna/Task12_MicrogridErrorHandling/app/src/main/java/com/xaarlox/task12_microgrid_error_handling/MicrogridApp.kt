package com.xaarlox.task12_microgrid_error_handling

import android.app.Application

class MicrogridApp : Application() {
    val container by lazy { AppContainer(this) }
}