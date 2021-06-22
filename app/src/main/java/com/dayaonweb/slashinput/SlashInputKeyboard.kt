package com.dayaonweb.slashinput

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.google.android.material.button.MaterialButton

class SlashInputKeyboard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attributeSet, defStyle, defStyleRes), View.OnClickListener {

    // The input keys (buttons)
    private var keys: List<Int> = listOf(
        R.id.button_0,
        R.id.button_1,
        R.id.button_2,
        R.id.button_3,
        R.id.button_4,
        R.id.button_5,
        R.id.button_6,
        R.id.button_7,
        R.id.button_8,
        R.id.button_9,
        R.id.button_dot,
        R.id.button_clear,
    )


    // Communication link to the input edit text
    private var inputConnection: InputConnection? = null

    // Customizable parameters
    private var textColor: Int
    private var keyboardBackgroundColor: Int
    private var isDotKeyVisible: Boolean
    private var isClearKeyVisible: Boolean
    private var textFont: Int
    private var clearDrawable: Int
    private var clearDrawableColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.SlashInputKeyboard,
            defStyle,
            defStyleRes
        ).apply {
            textColor = getColor(
                R.styleable.SlashInputKeyboard_textColor,
                resources.getColor(R.color.slash_green, null)
            )
            keyboardBackgroundColor = getColor(
                R.styleable.SlashInputKeyboard_backgroundColor,
                resources.getColor(R.color.white, null)
            )
            clearDrawable = getResourceId(
                R.styleable.SlashInputKeyboard_clearDrawable,
                R.drawable.outline_backspace_black_24dp
            )
            clearDrawableColor = getResourceId(
                R.styleable.SlashInputKeyboard_clearDrawableColor,
                R.color.slash_green
            )
            textFont = getResourceId(R.styleable.SlashInputKeyboard_textFont, -1)
            isDotKeyVisible = getBoolean(R.styleable.SlashInputKeyboard_isDotVisible, true)
            isClearKeyVisible = getBoolean(R.styleable.SlashInputKeyboard_isClearVisible, true)
        }
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        initializeKeyboard()
    }

    private fun initializeKeyboard() {
        setupKeysListener()
        setInputTextColor(textColor)
        setKeyboardBackgroundColor(keyboardBackgroundColor)
        setDotAvailable(isDotKeyVisible)
        setClearAvailable(isClearKeyVisible)
        setTextFont(textFont)
        setClearDrawable(clearDrawable)
        setClearDrawableColor(clearDrawableColor)
    }


    private fun setupKeysListener() {
        keys.forEach { keyId ->
            val keyButton = findViewById<MaterialButton>(keyId)
            keyButton.setOnClickListener(this)
        }
    }

    private fun invalidateLayout() {
        invalidate()
        requestLayout()
    }

    fun setInputTextColor(@ColorInt color: Int) {
        for (keyId in keys) {
            if (keyId != R.id.button_clear) {
                val keyButton = findViewById<MaterialButton>(keyId)
                keyButton.setTextColor(color)
            }
        }
        invalidateLayout()
    }

    fun setKeyboardBackgroundColor(@ColorInt color: Int) {
        children.forEach { view ->
            if (view is LinearLayout) {
                view.setBackgroundColor(color)
            }
        }
        invalidateLayout()
    }

    fun setDotAvailable(isAvailable: Boolean) {
        val keyDotId = keys.find { keyId ->
            keyId == R.id.button_dot
        } ?: return
        val keyDot = findViewById<MaterialButton>(keyDotId)
        keyDot.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
        keyDot.isClickable = isAvailable
        invalidateLayout()
    }

    fun setClearAvailable(isAvailable: Boolean) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
        keyClear.isClickable = isAvailable
        invalidateLayout()
    }


    fun setInputConnection(inputConnection: InputConnection?) {
        this.inputConnection = inputConnection
    }

    fun setClearDrawable(@DrawableRes drawableRes: Int) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.icon = ResourcesCompat.getDrawable(resources, drawableRes, null)
        invalidateLayout()
    }

    fun setClearDrawableColor(@ColorRes colorRes: Int) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.iconTint = ResourcesCompat.getColorStateList(resources, colorRes, null)
        invalidateLayout()
    }

    fun setTextFont(@FontRes fontRes: Int) {
        if (fontRes == -1) return
        for (keyId in keys) {
            if (keyId != R.id.button_clear) {
                val keyButton = findViewById<MaterialButton>(keyId)
                keyButton.typeface = ResourcesCompat.getFont(context, fontRes)
            }
        }
        invalidateLayout()
    }

    // Called for a click to entire keyboard
    override fun onClick(view: View?) {
        if (inputConnection == null || view == null) return

        // If delete is pressed, handle accordingly
        if (view.id == R.id.button_clear) {
            val selectedText = inputConnection?.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText))
                inputConnection?.deleteSurroundingText(1, 0) // Delete previous character
            else
                inputConnection?.commitText("", 1) // Delete selection
        } else {
            // Some other key is pressed
            //val pressedKeyValue = keyIdToString[view.id]
            var pressedKeyValue = resources.getResourceEntryName(view.id).split("_")[1]
            if (pressedKeyValue == "dot")
                pressedKeyValue = "." // If key is called "dot", make it as "."
            inputConnection?.commitText(pressedKeyValue, 1)
        }
    }

    companion object {
        private const val TAG = "SlashInputKeyboard"
    }

}