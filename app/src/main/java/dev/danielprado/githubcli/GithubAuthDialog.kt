package dev.danielprado.githubcli

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import dev.danielprado.githubcli.databinding.DialogGithubAuthBinding
import java.lang.IllegalStateException

const val GH_CLI_ID = "c3daa1d5d1d813ca449d"
private const val GH_URL_LOGIN = "https://github.com/login/oauth/authorize"
private const val GH_LOGIN_STATE = "MyGitHubApplication"

typealias OnCodeObtained = (String) -> Unit

class GithubAuthDialog(val onCodeObtained: OnCodeObtained): DialogFragment() {
    lateinit private var layout: DialogGithubAuthBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            layout = DialogGithubAuthBinding.inflate(inflater)
            val builder = AlertDialog.Builder(it)
            builder.setView(layout.root)
            val dialog = builder.create()
            layout.webview.apply {
                settings.javaScriptEnabled = true
                setWebViewClient(object: WebViewClient() {
                    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                        if (url?.contains("blog.danielprado.dev") ?: false) {
                            val uri = Uri.parse(url)
                            if (uri.getQueryParameter("state") == GH_LOGIN_STATE) {
                                onCodeObtained.invoke(uri.getQueryParameter("code") ?: "")
                            }
                            dialog.dismiss()
                        }
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }
                })
                loadUrl("$GH_URL_LOGIN?client_id=$GH_CLI_ID&state=$GH_LOGIN_STATE&allow_signup=false")
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}