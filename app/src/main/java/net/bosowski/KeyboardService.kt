package net.bosowski

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.ExtractedTextRequest
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.Observer
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
import net.bosowski.predictionSettings.PredictionSettingsViewModel
import net.bosowski.authentication.LoginViewModel
import net.bosowski.predictionSettings.PredictionSettingModel
import net.bosowski.userStats.StatsViewModel
import net.bosowski.utlis.Constants

class KeyboardService : View.OnClickListener, InputMethodService() {

    private var capsOn = false
    private lateinit var mainView: View
    private lateinit var suggestions: Sequence<View>

    private lateinit var statsViewModel: StatsViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var predictionSettingsViewModel: PredictionSettingsViewModel

    private lateinit var app: KeyboardGPTApp

    lateinit var primaryKeyboard: LinearLayout
    lateinit var symbolsKeyboard: LinearLayout
    lateinit var spinner: Spinner

    private var observer: Observer<ArrayList<PredictionSettingModel>>? = null

    @Override
    override fun onCreateInputView(): View {
        app = application as KeyboardGPTApp

        mainView = layoutInflater.inflate(R.layout.keyboard_view_primary_english, null)
        suggestions = mainView.findViewById<LinearLayout>(R.id.suggestions_layout).children
        spinner = mainView.findViewById(R.id.spinner)

        statsViewModel = app.statsViewModel
        loginViewModel = app.loginViewModel
        predictionSettingsViewModel = app.predictionSettingsViewModel

        primaryKeyboard = (layoutInflater.inflate(
            R.layout.keyboard_view_primary_english, null
        ) as ViewGroup).findViewById(R.id.keyboard)
        (primaryKeyboard.parent as ViewGroup).removeView(primaryKeyboard)
        symbolsKeyboard = (layoutInflater.inflate(
            R.layout.keyboard_view_symbols_english, null
        ) as ViewGroup).findViewById(R.id.keyboard)
        (symbolsKeyboard.parent as ViewGroup).removeView(symbolsKeyboard)

        observer = Observer<ArrayList<PredictionSettingModel>> {
            spinner.adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                predictionSettingsViewModel.predictionSettings.value!!.map { it.text })
        }

        predictionSettingsViewModel.predictionSettings.observeForever(observer!!)

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
        statsViewModel.incrementKeyStrokes(v.tag.toString())

        when (v.tag) {
            "DEL", "ENTER", "SPACE", "TAB" -> handleSpecialKey(v)
            "SYMBOLS" -> switchToSymbols(mainView as ViewGroup)
            "PRIMARY" -> switchToPrimary(mainView as ViewGroup)
            "CAPS_LOCK" -> toggleCaps(v)
            "AI_CALL" -> aiCall(v)
            else -> handleRegularKey(v as TextView)
        }
    }

    private fun handleSpecialKey(v: View) {
        sendDownUpKeyEvents(KeyEvent.keyCodeFromString(v.tag.toString()))
    }

    private fun handleRegularKey(v: TextView) {
        currentInputConnection.commitText(v.text, 1)
    }

    private fun switchToSymbols(keyboardRoot: ViewGroup) {
        keyboardRoot.removeView(mainView.findViewById(R.id.keyboard))
        keyboardRoot.addView(symbolsKeyboard)

    }

    private fun switchToPrimary(keyboardRoot: ViewGroup) {
        keyboardRoot.removeView(mainView.findViewById(R.id.keyboard))
        keyboardRoot.addView(primaryKeyboard)
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

    private fun aiCall(view: View) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                updateSuggestion()
            }
        }
    }
}