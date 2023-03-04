package net.bosowski.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import net.bosowski.R
import android.widget.Button


class KeyboardService: View.OnClickListener, InputMethodService() {


    @Override
    override fun onCreateInputView(): View {
        val myKeyboardView: View = layoutInflater.inflate(R.layout.keyboard_view, null)
        val btnA: Button = myKeyboardView.findViewById(R.id.keyboard_button_g)
        btnA.setOnClickListener(this)
        //ADD ALL THE OTHER LISTENERS HERE FOR ALL THE KEYS
        return myKeyboardView
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}