package net.bosowski

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import net.bosowski.keyboard.CallbackTarget
import net.bosowski.keyboard.stats.FirebaseStatsStore

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
        FirebaseStatsStore.registerCallbackTarget(this)
    }

    override fun onDataChanged() {
        val stats = FirebaseStatsStore.find(userId)
        findViewById<TextView>(R.id.keystrokes).text =
            getString(R.string.total_keystrokes, stats?.buttonClicks?.size ?: 0)
        findViewById<TextView>(R.id.completionClicks).text =
            getString(R.string.completion_clicks, stats?.completionsUsed ?: 0)
    }

}