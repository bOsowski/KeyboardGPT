package net.bosowski.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
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
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.utlis.Constants
import net.bosowski.utlis.Observer
import java.text.DecimalFormat

class UserOverview : Observer, AppCompatActivity() {

    private lateinit var app: KeyboardGPTApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as KeyboardGPTApp
        setContentView(R.layout.activity_user_overview)
        app.statsStore.registerObserver(this)
    }

    override fun onStart() {
        super.onStart()
        fetchUserData()
    }

    private fun fetchUserData(){
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val response = HttpClient().get("${Constants.CHATTERGPT_SERVER_URL}/api/user/info") {
                        bearerAuth(app.idToken ?: "")
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
        val stats = app.statsStore.find()
        findViewById<TextView>(R.id.keystrokes).text =
            getString(R.string.total_keystrokes, stats?.buttonClicks?.map { it.value }?.sum() ?: 0)
        findViewById<TextView>(R.id.completionClicks).text =
            getString(R.string.completion_clicks, stats?.completionsUsed ?: 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user_overview, menu)
        menu!!.findItem(R.id.settings_button).setOnMenuItemClickListener { _ ->
            val launcherIntent = Intent(this, PredictionSettingsListActivity::class.java)
            startActivity(launcherIntent)
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

}