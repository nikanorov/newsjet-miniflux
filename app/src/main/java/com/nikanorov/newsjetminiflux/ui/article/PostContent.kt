package com.nikanorov.newsjetminiflux.ui.article

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import com.nikanorov.newsjetminiflux.R
import com.nikanorov.newsjetminiflux.model.Metadata
import com.nikanorov.newsjetminiflux.model.Post
import com.nikanorov.newsjetminiflux.ui.utils.getCssPostFixes

private val defaultSpacerSize = 16.dp

@Composable
fun PostContent(
    post: Post, modifier: Modifier = Modifier, state: LazyListState = rememberLazyListState()
) {

    LazyColumn(
        modifier = modifier.padding(horizontal = defaultSpacerSize),
        state = state,
    ) {

        postContentItems(post)

    }

}

fun LazyListScope.postContentItems(post: Post) {

    item {
        Spacer(Modifier.height(defaultSpacerSize))
        PostHeaderImage(post)
    }
    item {

        val context = LocalContext.current

        Text(text = post.title, style = MaterialTheme.typography.h5, modifier = Modifier.clickable {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.url)))
        })
        Spacer(Modifier.height(8.dp))
    }
    post.subtitle?.let { subtitle ->
        item {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = subtitle, style = MaterialTheme.typography.body2, lineHeight = 20.sp
                )
            }
            Spacer(Modifier.height(defaultSpacerSize))
        }
    }
    item {
        PostMetadata(post.metadata)
        Spacer(Modifier.height(24.dp))
    }
    item {
        PostContentView(post)
    }

    item {
        Spacer(Modifier.height(48.dp))
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PostContentView(post: Post) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()

    AndroidView(factory = {
        WebView(context).apply {

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url ?: return false
                    context.startActivity(Intent(Intent.ACTION_VIEW, url))
                    return true
                }
            }
            settings.javaScriptEnabled = true


            //issue: https://issuetracker.google.com/issues/237785596
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isDarkMode) {
                settings.forceDark = FORCE_DARK_ON
            }

            loadDataWithBaseURL(null, getCssPostFixes() + post.content, "text/html", "UTF-8", null)
        }
    })
}

@Composable
private fun PostHeaderImage(post: Post) {
    val imageModifier = Modifier.heightIn(min = 180.dp).fillMaxWidth().clip(shape = MaterialTheme.shapes.medium)
    post.imageId?.let {
        Image(
            painter = painterResource(post.imageId), contentDescription = null, // decorative
            modifier = imageModifier, contentScale = ContentScale.Crop
        )
    }

    Spacer(Modifier.height(defaultSpacerSize))
}

@Composable
private fun PostMetadata(metadata: Metadata) {
    val typography = MaterialTheme.typography
    Row(
        // Merge semantics so accessibility services consider this row a single element
        modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null, // decorative
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = metadata.author.name, style = typography.caption, modifier = Modifier.padding(top = 4.dp)
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(
                        id = R.string.article_post_min_read, formatArgs = arrayOf(
                            metadata.date, metadata.readTimeMinutes
                        )
                    ), style = typography.caption
                )
            }
        }
    }
}
