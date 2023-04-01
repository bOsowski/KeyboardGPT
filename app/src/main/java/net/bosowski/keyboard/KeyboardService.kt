package net.bosowski.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import net.bosowski.R
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.children


class KeyboardService: View.OnClickListener, InputMethodService() {


    @Override
    override fun onCreateInputView(): View {
        val myKeyboardView: View = layoutInflater.inflate(R.layout.keyboard_view, null)
        val parentLayout = myKeyboardView.findViewById<LinearLayout>(R.id.keyboard)
        val buttonRows = parentLayout.children
        for(row in buttonRows){
            for(button in (row as LinearLayout).children){
                button.setOnClickListener{
                    sendDownUpKeyEvents(KeyEvent.keyCodeFromString((button as Button).text.toString()))
                }
            }
        }
        return myKeyboardView
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}