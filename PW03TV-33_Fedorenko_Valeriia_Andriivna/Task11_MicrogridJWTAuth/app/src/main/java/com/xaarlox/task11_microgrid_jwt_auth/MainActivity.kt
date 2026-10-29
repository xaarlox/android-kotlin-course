package com.xaarlox.task11_microgrid_jwt_auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xaarlox.task11_microgrid_jwt_auth.ui.AppRoot
import com.xaarlox.task11_microgrid_jwt_auth.ui.AppViewModelFactory
import com.xaarlox.task11_microgrid_jwt_auth.ui.login.AuthViewModel
import com.xaarlox.task11_microgrid_jwt_auth.ui.main.PortalViewModel
import com.xaarlox.task11_microgrid_jwt_auth.ui.theme.EcoGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoGridTheme {
                val container = (application as MicrogridApp).container
                val factory = remember { AppViewModelFactory(container) }
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                val portalViewModel: PortalViewModel = viewModel(factory = factory)
                AppRoot(authViewModel, portalViewModel)
            }
        }
    }
}