package net.bosowski.keyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import net.bosowski.R
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import java.lang.reflect.Field


class KeyboardService : View.OnClickListener, InputMethodService() {

    private var capsOn = false
    private lateinit var mainView: View

    private var idToken: String? = null

    @Override
    override fun onCreateInputView(): View {
        mainView = layoutInflater.inflate(R.layout.keyboard_view, null)
        idToken = getSharedPreferences("net.bosowski.shared", Context.MODE_PRIVATE).getString("idToken", null)
        return mainView
    }

    /**
     * Called by the caps button.
     */
    fun toggleCaps(v: View) {
        capsOn = !capsOn
        for (row in mainView.findViewById<LinearLayout>(R.id.keyboard).children) {
            for (button in (row as LinearLayout).children) {
                if(button is Button && button.text.length == 1){
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
    fun sendTagAsEvent(v: View){
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

    fun onClickSuggestion(v: View){
        v as TextView
        if(v.text.isNotEmpty()){
            currentInputConnection.commitText(idToken, 1)
        }
    }

}