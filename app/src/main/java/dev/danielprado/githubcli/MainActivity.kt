package dev.danielprado.githubcli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import dev.danielprado.githubcli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit private var layoutBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)

        layoutBinding.btnLogIn.setOnClickListener {
            GithubAuthDialog().show(supportFragmentManager, null)
        }

//        layoutBinding.webView.apply {
//            settings.javaScriptEnabled = true
//            setWebViewClient(object : WebViewClient() {
//                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
//                    if (url?.contains("blog.danielprado.dev") ?: false) {
//                        processCode(url!!)
//                    }
//                    super.doUpdateVisitedHistory(view, url, isReload)
//                }
//            })

//        }
    }

    private fun processCode(code: String) {
        Log.i("CUSTOM_DFP", code)
    }
}