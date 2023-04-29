package net.bosowski.keyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.ExtractedTextRequest
import net.bosowski.R
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.ktor.client.request.setBody
import net.bosowski.MainActivity
import net.bosowski.keyboard.stats.FirebaseStatsStore
import net.bosowski.keyboard.stats.StatsModel
import net.bosowski.keyboard.stats.StatsStore
import net.bosowski.utlis.Constants

class KeyboardService : View.OnClickListener, InputMethodService() {

    private var capsOn = false
    private lateinit var mainView: View
    private lateinit var suggestions: Sequence<View>

    private var idToken: String? = null
    private var email: String? = null
    private var userId: String? = null
    private var userStats: StatsModel? = null


    @Override
    override fun onCreateInputView(): View {
        mainView = layoutInflater.inflate(R.layout.keyboard_view, null)
        val sharedPrefs = getSharedPreferences("net.bosowski.shared", Context.MODE_PRIVATE)
        idToken = sharedPrefs.getString("idToken", null)
        email = sharedPrefs.getString("email", null)
        userId = sharedPrefs.getString("userId", null)

        suggestions = mainView.findViewById<LinearLayout>(R.id.suggestions_layout).children

        if (idToken != null && email != null && userId != null) {
            userStats =  FirebaseStatsStore.find(userId!!) ?: StatsModel(null, email!!, userId!!, HashMap(),  0)
        }
        return mainView
    }

    /**
     * Called by the caps button.
     */
    private fun toggleCaps(v: View) {
        capsOn = !capsOn
        for (row in mainView.findViewById<LinearLayout>(R.id.keyboard).children) {
            for (button in (row as LinearLayout).children) {
                if (button is Button && button.text.length == 1) {
                    if (capsOn) {
                        button.text = button.text.toString().uppercase()
                    } else {
                        button.text = button.text.toString().lowercase()
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        v as TextView

        if (userStats != null) {
            userStats!!.buttonClicks[v.text.toString()] =
                userStats!!.buttonClicks.getOrDefault(v.text.toString(), 0) + 1
            FirebaseStatsStore.set(userStats!!)
        }

        when (v.tag) {
            // Called by special keys that can have their tag translated to keyCode, eg. "DEL" or "CAPS_LOCK".
            in listOf("DEL", "ENTER", "SPACE", "TAB") -> {
                sendDownUpKeyEvents(KeyEvent.keyCodeFromString(v.tag.toString()))
                return
            }

            "CAPS_LOCK" -> {
                toggleCaps(v)
            }

            else -> {
                currentInputConnection.commitText(v.text, 1)
            }
        }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                if (userStats != null) {
                    updateSuggestion()
                }
            }
        }
    }

    private suspend fun updateSuggestion() {
        val allText = getAllText()
        val client = HttpClient()
        val response = client.post("${Constants.CHATTERGPT_SERVER_URL}/api/ai/autocompleteRequest") {
            bearerAuth(idToken ?: "")
            setBody(allText)
        }
        val choiceJsonArray =
            JsonParser.parseString(response.bodyAsText()).asJsonObject.get("choices").asJsonArray

        val choices = choiceJsonArray.map { it.asJsonObject.get("text").asString }.toSet()

        suggestions.forEachIndexed { index, view ->
            view as TextView
            if (choices.size <= index) {
                view.text = ""
            } else {
                view.text =
                    choices.elementAt(index).replace("\n\n\"", "").reversed().replace("\"", "")
                        .reversed()
            }
        }
    }

    // Called by the suggestion buttons.
    fun onClickSuggestion(v: View) {
        v as TextView

        userStats!!.completionsUsed++
        FirebaseStatsStore.set(userStats!!)

        if (v.text.isNotEmpty()) {
            replaceAllTextWith(v.text.toString())
        }
    }

    private fun replaceAllTextWith(replacement: String) {
        currentInputConnection.setSelection(0, getAllText().length)
        currentInputConnection.commitText(replacement, replacement.length)
    }

    private fun getAllText(): String {
        val allText = currentInputConnection.getExtractedText(ExtractedTextRequest(), 0)
        return allText?.text?.toString() ?: ""
    }
}