package com.test.remake.activitys

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.test.remake.R
import com.test.remake.utils.Constants.TAG

class WebActivity : AppCompatActivity(){
    lateinit var webview : WebView
    lateinit var progressbar : ProgressBar
    lateinit var search_link : String
    lateinit var pre_btn : Button
    lateinit var next_btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "WebActivity - 로딩완료. / WebActivity Loaded")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val searchTerm = intent.getStringExtra("search_key").toString()
        search_link = "http://vitamin.or.kr/bbs/search.php?gr_id=additive&sfl=wr_subject%7C%7Cwr_content&stx=$searchTerm&device=mobile"
        webview = findViewById(R.id.webview1)
        progressbar = findViewById(R.id.progress1)
        webview.apply{
            webViewClient = WebViewClientClass()
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(this@WebActivity).apply{
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }
                    val dialog = Dialog(this@WebActivity).apply{
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }
                    newWebView.webChromeClient = object :WebChromeClient(){
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }
                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true

            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.displayZoomControls = true

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                settings.safeBrowsingEnabled = true
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                settings.mediaPlaybackRequiresUserGesture = false
            }
            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                settings.allowUniversalAccessFromFileURLs = true
            }

            settings.allowFileAccess = true
            fitsSystemWindows = true

        }
        webview.loadUrl(search_link)

        pre_btn = findViewById(R.id.pre_btn)
        next_btn = findViewById(R.id.next_btn)

        pre_btn.setOnClickListener{
            val CangoBack : Boolean = webview.canGoBack()
            if(CangoBack){
                webview.goBack()
            }else{
                Toast.makeText(this, "이전 페이지가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        next_btn.setOnClickListener {
            val CangoForward : Boolean = webview.canGoForward()
            if(CangoForward){
                webview.goForward()
            }else{
                Toast.makeText(this, "다음 페이지가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    inner class WebViewClientClass: WebViewClient(){
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            url: String?
        ): Boolean {
            view?.loadUrl(url!!)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressbar.visibility = ProgressBar.VISIBLE
            webview.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            progressbar.visibility = ProgressBar.GONE
            webview.visibility = View.VISIBLE
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            var builder : android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@WebActivity)
            var message = "SSL Certificate error"
            when(error?.primaryError){
                SslError.SSL_UNTRUSTED -> message = "THe certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "THe certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "THe certificate Hostname mismatch."
                SslError.SSL_NOTYETVALID -> message = "THe certificate is not tet valid"
            }
            message += "Do you want to continue away?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue",
                DialogInterface.OnClickListener{_ , _ -> handler?.proceed() })
            builder.setNegativeButton("cancel",
                DialogInterface.OnClickListener{ dialog, which -> handler?.cancel() })
            val dialog: android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }
}