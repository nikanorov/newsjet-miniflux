package com.nikanorov.newsjetminiflux.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nikanorov.newsjetminiflux.ui.feeds.FeedsRoute
import com.nikanorov.newsjetminiflux.ui.feeds.FeedsViewModel
import com.nikanorov.newsjetminiflux.ui.home.HomeRoute
import com.nikanorov.newsjetminiflux.ui.home.HomeViewModel
import com.nikanorov.newsjetminiflux.ui.settings.SettingsRoute
import com.nikanorov.newsjetminiflux.ui.settings.SettingsViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NewsjetNavGraph(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = JetnewsDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(JetnewsDestinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = getViewModel {
                parametersOf(-1L)
            }
            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }
        composable(JetnewsDestinations.HOME_ROUTE_WITH_FEED_ID, arguments = listOf(navArgument("feedId") { defaultValue = "-1" })
        ) {
            val feedId = it.arguments?.getString("feedId")?.toLong() ?: -1L

            val homeViewModel: HomeViewModel = getViewModel{
                parametersOf(feedId)
            }

            HomeRoute(
                homeViewModel = homeViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer
            )
        }

        composable(JetnewsDestinations.FEEDS_ROUTE) {
            val feedsViewModel: FeedsViewModel = getViewModel()

            FeedsRoute(
                feedsViewModel = feedsViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
                navController = navController,
            )
        }

        composable(JetnewsDestinations.SETTINGS_ROUTE) {
            val preferenceViewModel: SettingsViewModel = getViewModel()

            SettingsRoute(
                settingsViewModel = preferenceViewModel,
                isExpandedScreen = isExpandedScreen,
                openDrawer = openDrawer,
            )
        }
    }
}
