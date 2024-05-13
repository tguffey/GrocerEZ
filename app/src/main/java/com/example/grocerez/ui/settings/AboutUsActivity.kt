package com.example.grocerez.ui.settings

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerez.R

class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        val webView: WebView = findViewById(R.id.webview_youtube)
        val video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/EcBAATdYGuo?si=aZWjdq1nKHIuwVBm\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
        webView.loadData(video, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
    }
}
