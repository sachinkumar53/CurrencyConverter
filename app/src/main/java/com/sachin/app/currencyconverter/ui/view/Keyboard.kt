package com.sachin.app.currencyconverter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.FrameLayout
import com.sachin.app.currencyconverter.R
import com.sachin.app.currencyconverter.databinding.KeyboardBinding


class Keyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val binding = KeyboardBinding.inflate(LayoutInflater.from(context), this)
    var inputConnection: InputConnection? = null

    init {
        binding.apply {
            btn0.setOnClickListener(this@Keyboard)
            btn1.setOnClickListener(this@Keyboard)
            btn2.setOnClickListener(this@Keyboard)
            btn3.setOnClickListener(this@Keyboard)
            btn4.setOnClickListener(this@Keyboard)
            btn5.setOnClickListener(this@Keyboard)
            btn6.setOnClickListener(this@Keyboard)
            btn7.setOnClickListener(this@Keyboard)
            btn8.setOnClickListener(this@Keyboard)
            btn9.setOnClickListener(this@Keyboard)
            btnDot.setOnClickListener(this@Keyboard)
            btnBackspace.setOnClickListener(this@Keyboard)

            btnBackspace.setOnLongClickListener {
                inputConnection?.let {
                    val currentText = it.getExtractedText(ExtractedTextRequest(), 0).text
                    val beforeCursorText = it.getTextBeforeCursor(currentText.length, 0)
                    val afterCursorText = it.getTextAfterCursor(currentText.length, 0)
                    it.deleteSurroundingText(
                        beforeCursorText.length,
                        afterCursorText.length
                    )
                }
                true
            }
        }
    }

    override fun onClick(v: View?) {
        if (inputConnection == null) return

        if (v?.id == R.id.btn_backspace) {
            val selectedText = inputConnection?.getSelectedText(0)
            if (selectedText.isNullOrEmpty()) {
                inputConnection?.deleteSurroundingText(1, 0)
            } else {
                inputConnection?.commitText("", 1)
            }
        } else {
            inputConnection?.commitText((v as Button).text, 1)
        }
    }
}