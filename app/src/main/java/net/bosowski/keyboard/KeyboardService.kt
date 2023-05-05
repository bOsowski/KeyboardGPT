package net.bosowski.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.ExtractedTextRequest
import android.widget.ArrayAdapter
import net.bosowski.R
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.children
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
import net.bosowski.KeyboardGPTApp
import net.bosowski.authentication.LoginViewModel
import net.bosowski.models.PredictionSettingModel
import net.bosowski.models.StatsModel
import net.bosowski.stores.FirebasePredictionSettingStore
import net.bosowski.userStats.StatsViewModel
import net.bosowski.utlis.Constants
import net.bosowski.utlis.Observer

class KeyboardService : View.OnClickListener, InputMethodService(), Observer {

    private var capsOn = false
    private lateinit var mainView: View
    private lateinit var suggestions: Sequence<View>

    private lateinit var statsViewModel: StatsViewModel
    private lateinit var loginViewModel: LoginViewModel

    private lateinit var app: KeyboardGPTApp

    lateinit var primaryKeyboard: LinearLayout
    lateinit var symbolsKeyboard: LinearLayout
    lateinit var spinner: Spinner


    @Override
    override fun onCreateInputView(): View {
        app = application as KeyboardGPTApp
        mainView = layoutInflater.inflate(R.layout.keyboard_view_primary_english, null)

        statsViewModel = app.getStatsViewModel()
        loginViewModel = app.getLoginViewModel()

        primaryKeyboard = (layoutInflater.inflate(
            R.layout.keyboard_view_primary_english, null
        ) as ViewGroup).findViewById(R.id.keyboard)
        (primaryKeyboard.parent as ViewGroup).removeView(primaryKeyboard)
        symbolsKeyboard = (layoutInflater.inflate(
            R.layout.keyboard_view_symbols_english, null
        ) as ViewGroup).findViewById(R.id.keyboard)
        (symbolsKeyboard.parent as ViewGroup).removeView(symbolsKeyboard)

        suggestions = mainView.findViewById<LinearLayout>(R.id.suggestions_layout).children

        FirebasePredictionSettingStore.registerObserver(this)

        spinner = mainView.findViewById(R.id.spinner)
        onDataChanged()

        return mainView
    }

    override fun onDataChanged() {
        var predictionSettings = FirebasePredictionSettingStore.findAll()
        if (predictionSettings.isEmpty()) {
            predictionSettings =
                arrayListOf(PredictionSettingModel(text = "Rephrase the text"))
            FirebasePredictionSettingStore.create(predictionSettings.first())
        }

        spinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            predictionSettings.map { it.text })
    }

    /**
     * Called by the caps button.
     */
    fun toggleCaps(v: View) {
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

        statsViewModel.incrementKeyStrokes(v.tag.toString())

        val keyboardRoot = mainView as ViewGroup
        when (v.tag) {
            // Called by special keys that can have their tag translated to keyCode, eg. "DEL" or "CAPS_LOCK".
            in listOf("DEL", "ENTER", "SPACE", "TAB", "ENTER") -> {
                sendDownUpKeyEvents(KeyEvent.keyCodeFromString(v.tag.toString()))
            }

            "SYMBOLS" -> {
                keyboardRoot.removeView(mainView.findViewById(R.id.keyboard))
                keyboardRoot.addView(symbolsKeyboard)
            }

            "PRIMARY" -> {
                keyboardRoot.removeView(mainView.findViewById(R.id.keyboard))
                keyboardRoot.addView(primaryKeyboard)
            }

            "CAPS_LOCK" -> {
                toggleCaps(v)
            }

            else -> {
                currentInputConnection.commitText(v.text, 1)
            }
        }
    }

    private suspend fun updateSuggestion() {
        val userDefinition = spinner.selectedItem.toString()
        val allText = getAllText()
        val client = HttpClient()
        val response =
            client.post("${Constants.CHATTERGPT_SERVER_URL}/api/ai/autocompleteRequest") {
                bearerAuth(loginViewModel.idToken.value ?: "")
                setBody("${userDefinition}, given the following:\"${allText}\"")
            }
        val choicesJson = JsonParser.parseString(response.bodyAsText()).asJsonObject.get("choices")

        if (choicesJson != null) {
            val choiceJsonArray = choicesJson.asJsonArray

            val choices =
                choiceJsonArray.map { it.asJsonObject.get("text").asString.trim() }.toSet()

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
    }

    // Called by the suggestion buttons.
    fun onClickSuggestion(v: View) {
        v as TextView

        statsViewModel.incrementCompletions()

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

    fun apiCall(view: View) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                updateSuggestion()
            }
        }
    }
}