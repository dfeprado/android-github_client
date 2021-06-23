package dev.danielprado.githubcli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dev.danielprado.githubcli.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val GH_URL_CODE_EXCHANGE = "https://github.com/login/oauth/access_token"
private const val GH_CLI_SECRET = "065ad7b372c88a4898a0a175e428cabb9033d715"

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

        layoutBinding.btnLogIn.setOnClickListener {
            GithubAuthDialog() { processCode(it) }.show(supportFragmentManager, null)
        }
    }

    private fun processCode(code: String) {
        Log.i("GITHUB_LOGIN", "Your code is $code")

        // prepare the JSON for code exchange
        val codeExchangePost = GithubLoginCodeExchangePost(
                GH_CLI_ID,
                GH_CLI_SECRET,
                code
        );

        // prepare URL
        val url = URL(GH_URL_CODE_EXCHANGE)
//        HttpURLConnection
        val post = url.openConnection() as HttpsURLConnection
        post.apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            doOutput = true
        }
        CoroutineScope(Dispatchers.IO).launch {
            val postJson = Json.encodeToString<GithubLoginCodeExchangePost>(codeExchangePost)
            val out = DataOutputStream(post.outputStream)
            out.write(postJson.toByteArray())
            out.flush()
            out.close()


            if (post.responseCode == 200) {
                var response: String = ""
                try {
                    val input = BufferedReader(InputStreamReader(post.inputStream))
                    val content = StringBuffer()
                    var inputLine = input.readLine()
                    while (inputLine != null) {
                        content.append(inputLine)
                        inputLine = input.readLine()
                    }
                    input.close()
                    response = content.toString()
                } finally {
                    post.disconnect()
                }
                Log.i("GITHUB_LOG", response)
            } else {
                Log.i("GITHUB_LOG", "Failed to exchange code")
            }
        }
    }
}