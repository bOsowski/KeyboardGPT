package net.bosowski.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import net.bosowski.R
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.children


class KeyboardService : View.OnClickListener, InputMethodService() {

    private var capsOn = false

    @Override
    override fun onCreateInputView(): View {
        val myKeyboardView: View = layoutInflater.inflate(R.layout.keyboard_view, null)
        val parentLayout = myKeyboardView.findViewById<LinearLayout>(R.id.keyboard)
        val buttonRows = parentLayout.children
        for (row in buttonRows) {
            for (button in (row as LinearLayout).children) {
                button.setOnClickListener {
                    button as Button
                    if (button.tag.toString() == "CAPS_LOCK") {
                        capsOn = !capsOn
                        toggleCaps(buttonRows)
                    }
                    else if(button.tag.toString() in arrayOf("SPACE", "DEL")){
                        sendDownUpKeyEvents(KeyEvent.keyCodeFromString(button.tag.toString()))
                    }
                    else{
                        currentInputConnection.commitText(button.text, 1)
                    }
                }
            }
        }
        return myKeyboardView
    }

    private fun toggleCaps(rows: Sequence<View>) {
        for (row in rows) {
            for (button in (row as LinearLayout).children) {
                if(capsOn){
                    (button as Button).text = button.text.toString().uppercase()
                }
                else{
                    (button as Button).text = button.text.toString().lowercase()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}