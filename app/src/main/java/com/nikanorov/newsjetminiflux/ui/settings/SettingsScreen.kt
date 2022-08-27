package com.nikanorov.newsjetminiflux.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nikanorov.newsjetminiflux.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.cd_settings),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left
                    )
                },
                navigationIcon = if (!isExpandedScreen) {
                    {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                } else {
                    null
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        SettingsScreenContent(viewModel, isExpandedScreen, screenModifier)
    }
}

@Composable
private fun SettingsScreenContent(
    viewModel: SettingsViewModel,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    )
    {
        OutlinedTextField(
            value = uiState.minifluxApiUrl,
            onValueChange = { viewModel.updateURL(it) },
            placeholder = { Text(stringResource(R.string.api_url_hint)) },
            label = { Text(stringResource(R.string.api_url_title)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(

            ),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()


        )

        OutlinedTextField(
            value = uiState.minifluxApiToken,
            onValueChange = { viewModel.updateToken(it) },
            placeholder = { Text(stringResource(R.string.token_hint)) },
            label = { Text(stringResource(R.string.token_title)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        TextButton(onClick = {
            viewModel.saveData()
        }) {
            Text(text = stringResource(R.string.btn_save))
        }
    }
}