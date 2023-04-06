package com.dayaonweb.slashinput

import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.google.android.material.button.MaterialButton

class SlashInputKeyboard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attributeSet, defStyle, defStyleRes), View.OnClickListener,
    View.OnTouchListener{

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
    private var keyRippleColor: Int
    private var isDotKeyVisible: Boolean
    private var isClearKeyVisible: Boolean
    private var textFont: Int
    private var clearDrawable: Int
    private var clearDrawableColor: Int
    private var isHapticsEnabled: Boolean
    private var isAnimationEnabled: Boolean
    private var isRippleEnabled: Boolean

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.SlashInputKeyboard,
            defStyle,
            defStyleRes
        ).apply {
            textColor = getColor(
                R.styleable.SlashInputKeyboard_textColor,
                ContextCompat.getColor(context, R.color.black)
            )
            keyRippleColor = getResourceId(
                R.styleable.SlashInputKeyboard_keyRippleColor,
                R.color.grey
            )
            keyboardBackgroundColor = getColor(
                R.styleable.SlashInputKeyboard_backgroundColor,
                ContextCompat.getColor(context, android.R.color.transparent)
            )
            clearDrawable = getResourceId(
                R.styleable.SlashInputKeyboard_clearDrawable,
                R.drawable.outline_backspace_black_24dp
            )
            clearDrawableColor = getResourceId(
                R.styleable.SlashInputKeyboard_clearDrawableColor,
                R.color.black
            )
            textFont = getResourceId(R.styleable.SlashInputKeyboard_textFont, -1)
            isDotKeyVisible = getBoolean(R.styleable.SlashInputKeyboard_isDotVisible, true)
            isClearKeyVisible = getBoolean(R.styleable.SlashInputKeyboard_isClearVisible, true)
            isHapticsEnabled = getBoolean(R.styleable.SlashInputKeyboard_hapticsEnabled, true)
            isRippleEnabled = getBoolean(R.styleable.SlashInputKeyboard_rippleEnabled, false)
            isAnimationEnabled = getBoolean(R.styleable.SlashInputKeyboard_animationEnabled, true)
        }
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        initializeKeyboard()
    }

    private fun initializeKeyboard() {
        setupKeysListener()
        setInputTextColor(textColor)
        setKeyRippleColor(keyRippleColor)
        setKeyboardBackgroundColor(keyboardBackgroundColor)
        setDotAvailable(isDotKeyVisible)
        setClearAvailable(isClearKeyVisible)
        setTextFont(textFont)
        setClearDrawable(clearDrawable)
        setClearDrawableColor(clearDrawableColor)
        setHaptic(isHapticsEnabled)
        setRippleEnabled(isRippleEnabled)
        setAnimationEnabled(isAnimationEnabled)
    }


    private fun setupKeysListener() {
        keys.forEach { keyId ->
            val keyButton = findViewById<MaterialButton>(keyId)
            keyButton.setOnClickListener(this)
        }
    }

    private fun setupAnimationOnKeyListener() {
        keys.forEach { keyId ->
            val keyButton = findViewById<MaterialButton>(keyId)
            keyButton.setOnTouchListener(this)
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

    fun setKeyRippleColor(@ColorRes keyRippleColor: Int) {
        for (keyId in keys) {
            findViewById<MaterialButton>(keyId).rippleColor =
                ContextCompat.getColorStateList(context, keyRippleColor)
        }
        invalidateLayout()
    }


    fun setHaptic(isEnabled: Boolean) {
        isHapticsEnabled = isEnabled
    }

    fun setAnimationEnabled(isEnabled: Boolean) {
        isAnimationEnabled = isEnabled
        if (isAnimationEnabled)
            setupAnimationOnKeyListener()
    }

    fun setRippleEnabled(isEnabled: Boolean) {
        isRippleEnabled = isEnabled
        toggleRippleEffectOnKeys(isRippleEnabled)
    }


    private fun toggleRippleEffectOnKeys(enable: Boolean) {
        for (keyId in keys) {
            findViewById<MaterialButton>(keyId).background = if (enable) null
            else ContextCompat.getDrawable(context, android.R.color.transparent)
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

    // Called for a click to a specific key of the IME
    override fun onClick(view: View?) {
        if (inputConnection == null || view == null) return

        // Provide haptic feedback if enabled
        view.apply {
            isHapticFeedbackEnabled = isHapticsEnabled
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }

        // If delete is pressed, handle accordingly
        if (view.id == R.id.button_clear) {
            val selectedText = inputConnection?.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText))
                inputConnection?.deleteSurroundingText(1, 0) // Delete previous character
            else
                inputConnection?.commitText("", 1) // Delete selection
        } else {
            // Some other key is pressed
            var pressedKeyValue = resources.getResourceEntryName(view.id).split("_")[1]
            if (pressedKeyValue == "dot")
                pressedKeyValue = "." // If key is called "dot", make it as "."
            inputConnection?.commitText(pressedKeyValue, 1)
        }
    }

    // Handle key animation on touch event
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == ACTION_DOWN) {
            v?.animate()
                ?.scaleX(0.5f)
                ?.scaleY(0.5f)
                ?.setInterpolator(BounceInterpolator())
                ?.start()
        } else if (event?.action == ACTION_UP) {
            v?.animate()
                ?.scaleX(1.0f)
                ?.scaleY(1.0f)
                ?.setInterpolator(BounceInterpolator())
                ?.start()
        }
        return false
    }

}