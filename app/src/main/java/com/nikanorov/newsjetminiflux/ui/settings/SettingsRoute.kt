package com.nikanorov.newsjetminiflux.ui.settings

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable


@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {

    SettingsScreen(
        viewModel = settingsViewModel,
        isExpandedScreen = isExpandedScreen,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}
