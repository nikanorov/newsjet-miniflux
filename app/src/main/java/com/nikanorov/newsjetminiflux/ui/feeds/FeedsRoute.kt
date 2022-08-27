package com.nikanorov.newsjetminiflux.ui.feeds

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun FeedsRoute(
    feedsViewModel: FeedsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController
) {
    FeedsScreen(
        viewModel = feedsViewModel,
        isExpandedScreen = isExpandedScreen,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        navController = navController
    )
}
