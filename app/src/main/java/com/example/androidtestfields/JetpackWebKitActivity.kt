package com.example.androidtestfields

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.*
import java.util.concurrent.Executors

class JetpackWebKitActivity : AppCompatActivity() {
    private val webView by lazy { findViewById<WebView>(R.id.webView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack_web_view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            WebView.startSafeBrowsing(this) {
            }
        }
        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this) {

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.webViewClient
        }
        if (WebViewFeature.isFeatureSupported(WebViewFeature.GET_WEB_VIEW_CLIENT)) {
            WebViewCompat.getWebViewClient(webView)
        }


        //Proxy Controller
        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            val proxyController = ProxyController.getInstance()
            val proxyConfig = ProxyConfig.Builder()
                .addProxyRule("my.proxy.url")
                .addProxyRule("my.http.proxy", ProxyConfig.MATCH_HTTP)
                .addProxyRule("my.https.proxy", ProxyConfig.MATCH_HTTPS)
                .addProxyRule("my.fallback.proxy", ProxyConfig.MATCH_ALL_SCHEMES)
                .addBypassRule("some.website.com")
                .build()
            proxyController.setProxyOverride(proxyConfig, Executors.newSingleThreadExecutor()) {

            }
            proxyController.clearProxyOverride(Executors.newSingleThreadExecutor()) {

            }
        }

        //asset loader
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
        //Assets are hosted under https://appassets.androidplatform.net/assets/....
        //If the application's assets are in the "main/assets" folder this will read the file
        //from "main/assets/www/index.html" and load it as if it were hosted on:
        // https://appassets.androidplatform.net/assets/www/index.html
        webView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
    }
}