package com.example.webviewexample

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.gun0912.tedpermission.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URISyntaxException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private val BASE_URL = "http://210.120.112.114:4380/app/webviewTest1.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webViewClient = InAppWebViewClient()
        webView.webChromeClient = InAppChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.settings.useWideViewPort = true
        webView.loadUrl(BASE_URL)
    }

    class InAppChromeClient : WebChromeClient() {

    }

    inner class InAppWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            if (request != null) {
                if (request.url.toString().startsWith("tel:")) {
                    val phoneNum = Uri.parse(request.url.toString())
                    val intent = Intent(Intent.ACTION_DIAL, phoneNum)
                    startActivity(intent)
                    return true

                } else if(request.url.toString().startsWith("sms:")){
                    val smsUri = Uri.parse(request.url.toString())
                    val intent = Intent(Intent.ACTION_SENDTO, smsUri)
                    startActivity(intent)
                    return true
                }
            }
            view?.loadUrl(request?.url.toString())
            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}