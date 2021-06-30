package dev.danielprado.githubcli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import dev.danielprado.githubcli.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Serializable
data class GithubLoginCodeExchangePost(
    val client_id: String,
    val client_secret: String,
    val code: String
);

class MainActivity : AppCompatActivity() {
    lateinit private var layoutBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)
    }

    fun login(view: View) {
        GithubAuthenticator(
            "c3daa1d5d1d813ca449d",
            "065ad7b372c88a4898a0a175e428cabb9033d715",
            "MyTestApplication",
            ::onLoginSuccess,
            ::onLoginFail
        ).doLogin(supportFragmentManager)
    }

    private fun onLoginSuccess(token: Token) {
        Log.i("MAIN_LOGIN", token.value)
    }

    private fun onLoginFail(reason: String) {
        Log.i("MAIN_LOGIN", reason)
    }
}