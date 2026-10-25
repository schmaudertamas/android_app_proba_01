package hu.radar.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val radarUrl = "https://met.hu/idojaras/aktualis_idojaras/radar/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webViewRadar)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.evaluateJavascript(
                    """
                    (function() {
                        var header = document.querySelector('header');
                        if (header) header.style.display = 'none';
                        var footer = document.querySelector('footer');
                        if (footer) footer.style.display = 'none';
                    })();
                    """.trimIndent(), null
                )
            }
        }
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(radarUrl)

        btnStart.setOnClickListener {
            triggerRadar(start = true)
        }

        btnStop.setOnClickListener {
            triggerRadar(start = false)
        }
    }

    private fun triggerRadar(start: Boolean) {
        val script = if (start) {
            """
            (function() {
                var btn = document.querySelector('[title*="Lejátszás"], [aria-label*="Lejátszás"], .play, #play, #anim_play');
                if (btn) { btn.click(); }
            })();
            """.trimIndent()
        } else {
            """
            (function() {
                var btn = document.querySelector('[title*="Megállítás"], [title*="Leállítás"], [aria-label*="Leállítás"], .stop, #stop, #anim_stop');
                if (btn) { btn.click(); }
            })();
            """.trimIndent()
        }
        webView.evaluateJavascript(script, null)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
