package com.simplemobiletools.keyboard.services

import android.inputmethodservice.InputMethodService
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.simplemobiletools.commons.extensions.performHapticFeedback
import com.simplemobiletools.keyboard.R
import com.simplemobiletools.keyboard.helpers.MyKeyboard
import com.simplemobiletools.keyboard.views.MyKeyboardView

// based on https://www.androidauthority.com/lets-build-custom-keyboard-android-832362/
class SimpleKeyboardIME : InputMethodService(), MyKeyboardView.OnKeyboardActionListener {
    private var keyboard: MyKeyboard? = null
    private var keyboardView: MyKeyboardView? = null
    private var caps = false

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view_keyboard, null) as MyKeyboardView
        keyboard = MyKeyboard(this, R.xml.keys_layout)
        keyboardView!!.setKeyboard(keyboard!!)
        keyboardView!!.onKeyboardActionListener = this
        return keyboardView!!
    }

    override fun onPress(primaryCode: Int) {
        keyboardView?.performHapticFeedback()
    }

    override fun onRelease(primaryCode: Int) {}

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = currentInputConnection
        if (inputConnection != null) {
            when (primaryCode) {
                MyKeyboard.KEYCODE_DELETE -> {
                    val selectedText = inputConnection.getSelectedText(0)
                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0)
                    } else {
                        inputConnection.commitText("", 1)
                    }
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }
                MyKeyboard.KEYCODE_SHIFT -> {
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }
                MyKeyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                else -> {
                    var code = primaryCode.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    inputConnection.commitText(code.toString(), 1)
                }
            }
        }
    }

    override fun onText(text: CharSequence?) {}

    override fun swipeLeft() {}

    override fun swipeRight() {}

    override fun swipeDown() {}

    override fun swipeUp() {}
}
