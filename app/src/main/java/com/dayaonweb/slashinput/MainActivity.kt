package com.dayaonweb.slashinput

import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var inputEditText: TextInputEditText
    private lateinit var keyboard: SlashInputKeyboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hook views
        inputEditText = findViewById(R.id.etInput)
        keyboard = findViewById(R.id.keyboard)

        // prevent system keyboard from appearing when EditText is tapped
        inputEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        inputEditText.setTextIsSelectable(true)
        inputEditText.showSoftInputOnFocus = false

        // pass the InputConnection from the EditText to the keyboard
        val inputConnection = inputEditText.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(inputConnection)

        // Setup button listeners to change color dynamically
        findViewById<Button>(R.id.btnTextColor).setOnClickListener {
            keyboard.setInputTextColor(getColor(R.color.design_default_color_primary))
        }

        findViewById<Button>(R.id.btnBgColor).setOnClickListener {
            keyboard.setKeyboardBackgroundColor(getColor(R.color.black))
        }


    }
}