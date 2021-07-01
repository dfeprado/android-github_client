package dev.danielprado.githubcli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import dev.danielprado.androidauthenticatorsapp.GH_APP_ID
import dev.danielprado.androidauthenticatorsapp.GH_APP_SECRET
import dev.danielprado.githubcli.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit private var layoutBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)
    }

    fun login(view: View) {
        GithubAuthenticatorDialog(
                "MyTestApplication",
                GH_APP_ID,
                GH_APP_SECRET,
                ::onLoginSuccess,
                ::onLoginFail
        ).show(supportFragmentManager, null)
    }

    private fun onLoginSuccess(token: GithubAuthToken) {
        Log.i("MAIN_LOGIN", token.value)
    }

    private fun onLoginFail(reason: String) {
        Log.i("MAIN_LOGIN", reason)
    }
}