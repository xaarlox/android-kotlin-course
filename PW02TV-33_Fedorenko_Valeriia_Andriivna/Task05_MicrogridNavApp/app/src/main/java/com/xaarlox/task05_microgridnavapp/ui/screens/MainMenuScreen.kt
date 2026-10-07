package com.xaarlox.task05_microgridnavapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xaarlox.task05_microgridnavapp.R
import com.xaarlox.task05_microgridnavapp.data.UserProfile
import com.xaarlox.task05_microgridnavapp.ui.components.AdaptiveLayout
import com.xaarlox.task05_microgridnavapp.ui.components.AppButton
import com.xaarlox.task05_microgridnavapp.ui.components.AppHeader
import com.xaarlox.task05_microgridnavapp.ui.components.ScreenContainer

/**
 * Main menu: header, current user state, and two navigation buttons
 * On narrow screens, elements are arranged in a column; on wide screens, the header is on the left, buttons on the right
 *
 * @param profile current user profile
 * @param onNavigateToUserInfo navigation to the user info screen
 * @param onNavigateToForecast navigation to the power forecast screen
 */
@Composable
fun MainMenuScreen(
    profile: UserProfile,
    onNavigateToUserInfo: () -> Unit,
    onNavigateToForecast: () -> Unit
) {
    ScreenContainer {
        AdaptiveLayout(
            compact = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuHeaderSection(profile)
                    Spacer(Modifier.height(32.dp))
                    MenuButtonsSection(onNavigateToUserInfo, onNavigateToForecast)
                }
            },
            wide = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuHeaderSection(profile, Modifier.weight(1f))
                    MenuButtonsSection(
                        onNavigateToUserInfo,
                        onNavigateToForecast,
                        Modifier.weight(1f)
                    )
                }
            }
        )
    }
}

/**
 * Menu header block: "Main menu", name, and (if available) user email
 *
 * @param profile current profile
 * @param modifier external modifier
 */
@Composable
private fun MenuHeaderSection(profile: UserProfile, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AppHeader(text = stringResource(R.string.main_menu_title))
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.current_user, profile.name),
            textAlign = TextAlign.Center
        )
        if (profile.email.isNotBlank()) {
            Text(
                text = stringResource(R.string.current_email, profile.email),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Main menu buttons block
 *
 * @param onNavigateToUserInfo action for the "User Information" button
 * @param onNavigateToForecast action for the "SPP Power Forecast" button
 * @param modifier external modifier
 */
@Composable
private fun MenuButtonsSection(
    onNavigateToUserInfo: () -> Unit,
    onNavigateToForecast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppButton(text = stringResource(R.string.menu_user_info), onClick = onNavigateToUserInfo)
        Spacer(Modifier.height(16.dp))
        AppButton(text = stringResource(R.string.menu_forecast), onClick = onNavigateToForecast)
    }
}