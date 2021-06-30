package dev.danielprado.githubcli

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dev.danielprado.githubcli.databinding.DialogGithubAuthBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

typealias AuthSuccessCallback = (Token) -> Unit
typealias AuthFailCallback = (String) -> Unit

//private const val GH_CLI_SECRET = "065ad7b372c88a4898a0a175e428cabb9033d715"

data class Token(
        val value: String,
        val scope: List<String>,
        val type: String
);

internal class Parser {
    private fun read(stream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(stream))
        val buffer = StringBuffer()
        var inputLine = reader.readLine()
        while (inputLine != null) {
            buffer.append(inputLine)
            inputLine = reader.readLine()
        }
        stream.close()
        return buffer.toString()
    }

    fun parseCodeExchangeResponse(stream: InputStream): Token {
        return parseCodeExchangeResponse(read(stream))
    }

    fun parseCodeExchangeResponse(json: String): Token {
        val obj = JSONObject(json)
        return Token(
                obj["access_token"] as String,
                (obj["scope"] as String).split(","),
                obj["token_type"] as String
        )
    }
}

private class GithubAuthDialog(
        private val state: String,
        private val applicationId: String,
        val onCodeObtained: (String) -> Unit,
        val onError: AuthFailCallback?
): DialogFragment() {
//    lateinit private var layout: DialogGithubAuthBinding
    private val loginUrl = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val webview = WebView(requireContext())
            val dialog = AlertDialog.Builder(it).setView(webview).create()
            webview.apply {
                settings.javaScriptEnabled = true
                setWebViewClient(object : WebViewClient() {
                    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                        if (url != null) {
                            val uri = Uri.parse(url)
                            val code = uri.getQueryParameter("code") ?: "";
                            if (code.isNotEmpty()) {
                                if (uri.getQueryParameter("state") == state)
                                    onCodeObtained(code!!)
                                dialog.dismiss()
                            }
                            else {
                                TODO("treat error")
                            }
                        }
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }
                })
                loadUrl("$loginUrl?client_id=$applicationId&state=$state&allow_signup=false")
            }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class GithubAuthenticator(
        private val applicationId: String,
        private val applicationSecret: String,
        private val state: String,
        var onAuthSuccess: AuthSuccessCallback,
        var onAuthFail: AuthFailCallback? = null
) {
    init {
        if (applicationId.trim().isEmpty())
            throw IllegalArgumentException("applicationId cannot be empty")
    }
    private val codeExchangeURL = "https://github.com/login/oauth/access_token"
    private val dialogToken = "github_login_dialog"

    suspend private fun exchangeCodeForToken(reqBody: String) {
        return withContext(Dispatchers.IO) {
            val url = URL(codeExchangeURL)
            val http = (url.openConnection() as HttpsURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                outputStream.write(reqBody.toByteArray())
            }

            if (http.responseCode == 200)
                onAuthSuccess(Parser().parseCodeExchangeResponse(http.inputStream))
            else
                onAuthFail?.invoke(http.responseMessage)
        }
    }

    private fun onCodeObtained(code: String) {
        // request body
        // https://stleary.github.io/JSON-java/index.html
        val codeExchangeReqBody = JSONObject(mapOf<String, Any>(
                "client_id" to applicationId,
                "client_secret" to applicationSecret,
                "code" to code
        )).toString()
        CoroutineScope(Dispatchers.Main).launch {
            exchangeCodeForToken(codeExchangeReqBody)
        }
    }

    fun doLogin(manager: FragmentManager) {
        GithubAuthDialog(state, applicationId, ::onCodeObtained, onAuthFail)
                .show(manager, dialogToken)
    }
}