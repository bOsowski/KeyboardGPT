package net.bosowski.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.bosowski.BuildConfig.SERVER_URL
import net.bosowski.KeyboardGPTApp
import net.bosowski.R
import net.bosowski.stores.FirebaseStatsStore
import net.bosowski.utlis.Constants
import net.bosowski.utlis.Observer
import java.text.DecimalFormat


class UserOverview : Observer, AppCompatActivity() {

    private lateinit var app: KeyboardGPTApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as KeyboardGPTApp
        setContentView(R.layout.activity_user_overview)
        FirebaseStatsStore.registerObserver(this) //todo: get rid of this.
    }

    override fun onStart() {
        super.onStart()
        fetchUserData()
    }

    private fun fetchUserData() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val response =
                        HttpClient().get("${Constants.CHATTERGPT_SERVER_URL}/api/user/info") {
                            val token = app.user.idToken
                            bearerAuth(token ?: "")
                        }
                    var availableCredits = 0f
                    if(response.bodyAsText().isNotBlank()){
                        val json = JsonParser.parseString(response.bodyAsText()).asJsonObject
                        availableCredits = json.get("availableCredits").asFloat
                    }
                    val df = DecimalFormat("#.####")
                    findViewById<TextView>(R.id.availableCredits).text = getString(
                        R.string.available_credits, df.format(availableCredits)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDataChanged() {
        val stats = FirebaseStatsStore.find()
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

    fun buyCredits(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(SERVER_URL))
        startActivity(browserIntent)
    }

}