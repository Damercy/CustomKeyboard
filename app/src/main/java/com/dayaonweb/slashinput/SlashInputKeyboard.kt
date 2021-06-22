package com.dayaonweb.slashinput

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton

class SlashInputKeyboard(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttribute: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttribute), View.OnClickListener {

    // The input keys (buttons)
    private lateinit var key0: MaterialButton
    private lateinit var key1: MaterialButton
    private lateinit var key2: MaterialButton
    private lateinit var key3: MaterialButton
    private lateinit var key4: MaterialButton
    private lateinit var key5: MaterialButton
    private lateinit var key6: MaterialButton
    private lateinit var key7: MaterialButton
    private lateinit var key8: MaterialButton
    private lateinit var key9: MaterialButton
    private lateinit var keyClear: MaterialButton
    private lateinit var keyDot: MaterialButton

    // HashMap containing mapping of keyID & keyText
    // Required to get which key is pressed & display corresponding string
    private var keyIdToString: HashMap<Int, String> = hashMapOf()


    // Communication link to the input edit text
    private lateinit var inputConnection: InputConnection

    init {
        initializeKeyboard(context)
    }

    private fun initializeKeyboard(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        hookKeys()
        setupKeysListener()
        populateKeysIdToStringMap()
    }

    private fun populateKeysIdToStringMap() {
        keyIdToString[R.id.button_0] = "0"
        keyIdToString[R.id.button_1] = "1"
        keyIdToString[R.id.button_2] = "2"
        keyIdToString[R.id.button_3] = "3"
        keyIdToString[R.id.button_4] = "4"
        keyIdToString[R.id.button_5] = "5"
        keyIdToString[R.id.button_6] = "6"
        keyIdToString[R.id.button_7] = "7"
        keyIdToString[R.id.button_8] = "8"
        keyIdToString[R.id.button_9] = "9"
        keyIdToString[R.id.button_dot] = "."
    }

    private fun setupKeysListener() {
        key0.setOnClickListener(this)
        key1.setOnClickListener(this)
        key2.setOnClickListener(this)
        key3.setOnClickListener(this)
        key4.setOnClickListener(this)
        key5.setOnClickListener(this)
        key6.setOnClickListener(this)
        key7.setOnClickListener(this)
        key8.setOnClickListener(this)
        key9.setOnClickListener(this)
        keyClear.setOnClickListener(this)
        keyDot.setOnClickListener(this)
    }

    private fun hookKeys() {
        key0 = findViewById(R.id.button_0)
        key1 = findViewById(R.id.button_1)
        key2 = findViewById(R.id.button_2)
        key3 = findViewById(R.id.button_3)
        key4 = findViewById(R.id.button_4)
        key5 = findViewById(R.id.button_5)
        key6 = findViewById(R.id.button_6)
        key7 = findViewById(R.id.button_7)
        key8 = findViewById(R.id.button_8)
        key9 = findViewById(R.id.button_9)
        keyClear = findViewById(R.id.button_clear)
        keyDot = findViewById(R.id.button_dot)
    }

    fun isDotAvailable(isAvailable: Boolean = true) {
        keyDot.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
        keyDot.isClickable = isAvailable
    }


    fun setInputConnection(inputConnection: InputConnection) {
        this.inputConnection = inputConnection
    }


    // Called for a click to entire keyboard
    override fun onClick(v: View?) {

    }

}