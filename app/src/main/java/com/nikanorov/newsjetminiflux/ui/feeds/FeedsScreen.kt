package com.nikanorov.newsjetminiflux.ui.feeds

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.nikanorov.newsjetminiflux.R
import com.nikanorov.newsjetminiflux.model.Feed
import com.nikanorov.newsjetminiflux.ui.JetnewsDestinations
import com.nikanorov.newsjetminiflux.ui.components.ListDivider
import com.nikanorov.newsjetminiflux.ui.theme.AppColors

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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(feeds) { idx, row ->
                FeedItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    feed = row,
                    navController = navController
                )
                Spacer(Modifier.height(16.dp))
                if (idx < feeds.lastIndex)
                    ListDivider()
            }

        }
    }
}

@Composable
private fun FeedItem(modifier: Modifier = Modifier, feed: Feed, navController: NavHostController) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(JetnewsDestinations.HOME_ROUTE + "/" + feed.id) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }) {
        UnreadCount(feed.unreadCount.toString())
        Text(feed.title, Modifier.padding(8.dp))
    }
}

@Composable
private fun UnreadCount(text: String) {
    val shape = RoundedCornerShape(8.dp)
    Text(
        text = text,
        color = AppColors.whiteDefault,
        modifier = Modifier
            .background(
                color = AppColors.Red400, shape = shape
            )
            .clip(shape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
