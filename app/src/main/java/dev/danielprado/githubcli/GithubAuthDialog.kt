package dev.danielprado.githubcli

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import dev.danielprado.githubcli.databinding.DialogGithubAuthBinding
import java.lang.IllegalStateException

const val GH_URL_LOGIN = "https://github.com/login/oauth/authorize"

class GithubAuthDialog: DialogFragment() {
    lateinit private var layout: DialogGithubAuthBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            layout = DialogGithubAuthBinding.inflate(inflater)
            layout.webview.apply {
                setWebViewClient(object: WebViewClient() {})
                loadUrl("$GH_URL_LOGIN?client_id=c3daa1d5d1d813ca449d&state=HelloWorld!&allow_signup=false")
            }

            val builder = AlertDialog.Builder(it)
            builder.setView(layout.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}