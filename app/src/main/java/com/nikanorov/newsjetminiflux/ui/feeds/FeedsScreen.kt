package com.nikanorov.newsjetminiflux.ui.feeds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.nikanorov.newsjetminiflux.R
import com.nikanorov.newsjetminiflux.model.Feed
import com.nikanorov.newsjetminiflux.ui.JetnewsDestinations

@Composable
fun FeedsScreen(
    viewModel: FeedsViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    navController: NavHostController,
) {
    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(R.string.cd_feeds),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left
            )
        }, navigationIcon = if (!isExpandedScreen) {
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
        }, backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp)
    }) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        FeedsScreenContent(viewModel, isExpandedScreen, navController, screenModifier)
    }
}

@Composable
private fun FeedsScreenContent(
    viewModel: FeedsViewModel,
    isExpandedScreen: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val feeds by viewModel.feeds.collectAsState()

    Column(modifier = modifier.navigationBarsPadding()) {
        LazyColumn {
            itemsIndexed(feeds) { idx, row ->
                FeedItem(row, navController)
            }

        }
    }
}

@Composable
private fun FeedItem(feed: Feed, navController: NavHostController) {
    Spacer(Modifier.height(16.dp))
    Text(feed.title, Modifier.clickable {
        navController.navigate(JetnewsDestinations.HOME_ROUTE + "/" + feed.id) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }.padding(8.dp))
}
