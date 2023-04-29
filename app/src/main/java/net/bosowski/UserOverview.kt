package net.bosowski

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.bosowski.keyboard.CallbackTarget
import net.bosowski.keyboard.stats.FirebaseStatsStore
import net.bosowski.utlis.Constants
import java.text.DecimalFormat

class UserOverview : CallbackTarget, AppCompatActivity() {

    private lateinit var idToken: String
    private lateinit var email: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_overview)

        val sharedPrefs = getSharedPreferences("net.bosowski.shared", Context.MODE_PRIVATE)
        idToken = sharedPrefs.getString("idToken", null).toString()
        email = sharedPrefs.getString("email", null).toString()
        userId = sharedPrefs.getString("userId", null).toString()
    }

    override fun onStart() {
        super.onStart()
        fetchUserData()
        FirebaseStatsStore.registerCallbackTarget(this)
    }

    private fun fetchUserData(){
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val response = HttpClient().get("${Constants.CHATTERGPT_SERVER_URL}/api/user/info") {
                        bearerAuth(idToken ?: "")
                    }
                    val json = JsonParser.parseString(response.bodyAsText()).asJsonObject
                    val df = DecimalFormat("#.####")
                    findViewById<TextView>(R.id.availableCredits).text = getString(R.string.available_credits, df.format(json.get("availableCredits").asFloat))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDataChanged() {
        val stats = FirebaseStatsStore.find(userId)
        findViewById<TextView>(R.id.keystrokes).text =
            getString(R.string.total_keystrokes, stats?.buttonClicks?.map { it.value }?.sum() ?: 0)
        findViewById<TextView>(R.id.completionClicks).text =
            getString(R.string.completion_clicks, stats?.completionsUsed ?: 0)
    }

}