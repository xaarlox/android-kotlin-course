package com.xaarlox.task05_microgridnavapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xaarlox.task05_microgridnavapp.ui.screens.ForecastDetailsScreen
import com.xaarlox.task05_microgridnavapp.ui.screens.ForecastInputScreen
import com.xaarlox.task05_microgridnavapp.ui.screens.MainMenuScreen
import com.xaarlox.task05_microgridnavapp.ui.screens.UserInfoScreen
import com.xaarlox.task05_microgridnavapp.viewmodel.UserViewModel

/**
 * - Application navigation graph (NavController + NavHost)
 * - The user profile is retrieved from the shared [UserViewModel], so the name and email
 *   are not passed through routes
 * - Only the number of panels is passed via a route argument
 */
@Composable
fun MicrogridAppNavigation(userViewModel: UserViewModel = viewModel()) {
    val navController = rememberNavController()
    val profile = userViewModel.profile

    NavHost(navController = navController, startDestination = Routes.MAIN_MENU) {

        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                profile = profile,
                onNavigateToUserInfo = { navController.navigate(Routes.USER_INFO) },
                onNavigateToForecast = { navController.navigate(Routes.FORECAST_INPUT) }
            )
        }

        composable(Routes.USER_INFO) {
            UserInfoScreen(
                initialProfile = profile,
                onSaveAndBack = { name, email ->
                    userViewModel.updateProfile(name, email)
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.FORECAST_INPUT) {
            ForecastInputScreen(
                userName = profile.name,
                onCalculate = { panels ->
                    navController.navigate(Routes.forecastDetails(panels))
                }
            )
        }

        composable(
            route = Routes.FORECAST_DETAILS_ROUTE,
            arguments = listOf(navArgument(Routes.ARG_PANELS) { type = NavType.IntType })
        ) { backStackEntry ->
            val panels = backStackEntry.arguments?.getInt(Routes.ARG_PANELS) ?: 0
            ForecastDetailsScreen(
                panelsCount = panels,
                onBackToForecast = { navController.popBackStack() },
                onBackToMenu = { navController.popBackStack(Routes.MAIN_MENU, inclusive = false) }
            )
        }
    }
}