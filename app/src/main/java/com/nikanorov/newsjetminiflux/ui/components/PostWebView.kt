package com.nikanorov.newsjetminiflux.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.nikanorov.newsjetminiflux.ui.utils.getCssPostFixes

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostWebView(modifier: Modifier = Modifier, content: String) {

    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()

    AndroidView(modifier = modifier, factory = {
        val webView = WebView(it).apply {

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

            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, isDarkMode)
                }
            } else {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    @Suppress("DEPRECATION")
                    WebSettingsCompat.setForceDark(settings, when {
                        isDarkMode -> WebSettingsCompat.FORCE_DARK_ON
                        else -> WebSettingsCompat.FORCE_DARK_OFF
                    })
                }
            }

            loadDataWithBaseURL(null, getCssPostFixes() + content, "text/html", "UTF-8", null)
        }

        //todo: workaround for https://issuetracker.google.com/issues/314821744. Remove when fixed.
        val wrapper = FrameLayout(it)
        wrapper.addView(webView)
        wrapper
    })
}
