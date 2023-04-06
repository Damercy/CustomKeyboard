package dev.dayaonweb.openime

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
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

class Keyboard @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attributeSet, defStyle, defStyleRes), View.OnClickListener,
    View.OnTouchListener {

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
    private var isHapticsEnabled: Boolean
    private var isAnimationEnabled: Boolean

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CustomKeyboard,
            defStyle,
            defStyleRes
        ).apply {
            textColor = getColor(
                R.styleable.CustomKeyboard_textColor,
                ContextCompat.getColor(context, R.color.black)
            )

            keyboardBackgroundColor = getColor(
                R.styleable.CustomKeyboard_backgroundColor,
                ContextCompat.getColor(context, android.R.color.transparent)
            )
            clearDrawable = getResourceId(
                R.styleable.CustomKeyboard_clearDrawable,
                R.drawable.outline_backspace_black_24dp
            )
            clearDrawableColor = getResourceId(
                R.styleable.CustomKeyboard_clearDrawableColor,
                R.color.black
            )
            textFont = getResourceId(R.styleable.CustomKeyboard_textFont, -1)
            isDotKeyVisible = getBoolean(R.styleable.CustomKeyboard_isDotVisible, true)
            isClearKeyVisible = getBoolean(R.styleable.CustomKeyboard_isClearVisible, true)
            isHapticsEnabled = getBoolean(R.styleable.CustomKeyboard_hapticsEnabled, true)
            isAnimationEnabled = getBoolean(R.styleable.CustomKeyboard_animationEnabled, true)
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
        setHaptic(isHapticsEnabled)
        setAnimationEnabled(isAnimationEnabled)
        setupAnimationOnKeyListener()
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

    /**
     * Set all the key's color in the keyboard.
     * @param color  A color value in the form 0xAARRGGBB.
     * Do not pass a resource ID. To get a color value from a resource ID, call getColor.
     */
    fun setInputTextColor(@ColorInt color: Int) {
        for (keyId in keys) {
            if (keyId != R.id.button_clear) {
                val keyButton = findViewById<MaterialButton>(keyId)
                keyButton.setTextColor(color)
            }
        }
        invalidateLayout()
    }

    /**
     * Toggle haptic feedback. Recommended to keep it enabled.
     * @param isEnabled  True if haptic feedback is required, false otherwise.
     */
    fun setHaptic(isEnabled: Boolean) {
        isHapticsEnabled = isEnabled
    }

    /**
     * Toggle animation on key press. Recommended to keep it enabled.
     * @param isEnabled  True if animation will be played, false otherwise.
     */
    fun setAnimationEnabled(isEnabled: Boolean) {
        isAnimationEnabled = isEnabled
    }


    /**
     * Set the entire keyboard's color i.e. background color.
     * @param color  A color value in the form 0xAARRGGBB.
     * Do not pass a resource ID. To get a color value from a resource ID, call getColor.
     */
    fun setKeyboardBackgroundColor(@ColorInt color: Int) {
        children.forEach { view ->
            if (view is LinearLayout) {
                view.setBackgroundColor(color)
            }
        }
        invalidateLayout()
    }

    /**
     * Toggle visibility of the dot key in the keyboard.
     * @param isAvailable  True if dot key will be visible, false otherwise.
     */
    fun setDotAvailable(isAvailable: Boolean) {
        val keyDotId = keys.find { keyId ->
            keyId == R.id.button_dot
        } ?: return
        val keyDot = findViewById<MaterialButton>(keyDotId)
        keyDot.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
        keyDot.isClickable = isAvailable
        invalidateLayout()
    }

    /**
     * Toggle visibility of the clear key in the keyboard.
     * @param isAvailable  True if clear key will be visible, false otherwise.
     */
    fun setClearAvailable(isAvailable: Boolean) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.visibility = if (isAvailable) View.VISIBLE else View.INVISIBLE
        keyClear.isClickable = isAvailable
        invalidateLayout()
    }


    /**
     * Pass the input connection of the corresponding edittext that this keyboard will be attached
     * to. For ex:
     * ```kotlin
     *  // Get view references first
     *  inputEditText = findViewById(R.id.etInput)
     *  keyboard = findViewById(R.id.keyboard)
     *
     *  // Get the input connection & pass it to the custom keyboard view
     *  val inputConnection = inputEditText.onCreateInputConnection(EditorInfo())
     *  keyboard.setInputConnection(inputConnection)
     * ```
     * @param inputConnection The created input connection of an edit text.
     */
    fun setInputConnection(inputConnection: InputConnection?) {
        this.inputConnection = inputConnection
    }

    /**
     * Set the clear (delete) key drawable / icon. Defaults to material icon's clear drawable.
     * @param drawableRes Drawable to use for the button's icon.
     */
    fun setClearDrawable(@DrawableRes drawableRes: Int) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.icon = ResourcesCompat.getDrawable(resources, drawableRes, null)
        invalidateLayout()
    }

    /**
     * Set the clear (delete) key drawable / icon color.
     * @param colorRes Tint list for the icon shown for this button.
     */
    fun setClearDrawableColor(@ColorRes colorRes: Int) {
        val keyClearId = keys.find { keyId ->
            keyId == R.id.button_clear
        } ?: return
        val keyClear = findViewById<MaterialButton>(keyClearId)
        keyClear.iconTint = ResourcesCompat.getColorStateList(resources, colorRes, null)
        invalidateLayout()
    }

    /**
     * Set the font style / typography of the keyboard.
     * @param fontRes  Resource ID of the desired font.
     */
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
        if (!isAnimationEnabled) return false
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