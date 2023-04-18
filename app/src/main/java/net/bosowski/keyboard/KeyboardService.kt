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

class KeyboardService : View.OnClickListener, InputMethodService() {

    private var capsOn = false
    private lateinit var mainView: View
    private lateinit var suggestions: Sequence<View>

    private var idToken: String? = null


    @Override
    override fun onCreateInputView(): View {
        mainView = layoutInflater.inflate(R.layout.keyboard_view, null)
        idToken = getSharedPreferences("net.bosowski.shared", Context.MODE_PRIVATE).getString(
            "idToken", null
        )
        suggestions = mainView.findViewById<LinearLayout>(R.id.suggestions_layout).children
        return mainView
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

    /**
     * Called by special keys that can have their tag translated to keyCode, eg. "DEL" or "CAPS_LOCK".
     */
    fun sendTagAsEvent(v: View) {
        sendDownUpKeyEvents(KeyEvent.keyCodeFromString(v.tag.toString()))
    }

    override fun onClick(v: View) {
        TODO("Not yet implemented")
    }

    /**
     * Used by dynamic textView elements.
     * eg. button from a-z. The text of those might change based on the capsOn state etc.
     */
    fun onClickButton(v: View) {
        v as TextView
        currentInputConnection.commitText(v.text, 1)
    }

    private suspend fun updateSuggestion() {
        val allText = getAllText()
        val client = HttpClient()
        val response = client.post("http://192.168.1.217:8080/api/ai/autocompleteRequest") {
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

    fun onClickSuggestion(v: View) {
        v as TextView
        if (v.text.isNotEmpty()) {
            replaceAllTextWith(v.text.toString())
        }
    }

    private fun replaceAllTextWith(replacement: String) {
//        currentInputConnection.setSelection(0, 0) //todo: check if this is required
        currentInputConnection.setSelection(0, getAllText().length)
        currentInputConnection.commitText(replacement, replacement.length)
    }

    private fun getAllText(): String {
        val allText = currentInputConnection.getExtractedText(ExtractedTextRequest(), 0)
        return allText?.text?.toString() ?: ""
    }
}