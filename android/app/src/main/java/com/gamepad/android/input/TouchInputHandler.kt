package com.gamepad.android.input

import android.view.MotionEvent
import com.gamepad.android.data.ButtonLayout
import com.gamepad.android.data.ControllerLayout
import com.gamepad.android.data.ControllerSettings
import com.gamepad.android.ui.utils.ResponsiveLayout
import kotlin.math.sqrt

/**
 * Handles touch input events for the controller
 * Detects button presses, stick movements, and multi-touch scenarios
 */
class TouchInputHandler(
    private val containerWidth: Float,
    private val containerHeight: Float,
    private val layout: ControllerLayout,
    private val settings: ControllerSettings
) {
    
    // Track which buttons are currently pressed
    private val pressedButtons = mutableSetOf<String>()
    
    // Track active stick touches by pointer ID
    private val activeSticksMap = mutableMapOf<Int, String>()
    
    // Track stick smoothing for center return
    private var leftStickX = 0f
    private var leftStickY = 0f
    private var rightStickX = 0f
    private var rightStickY = 0f
    
    data class TouchResult(
        val buttonPress: String? = null,
        val buttonRelease: String? = null,
        val stickUpdate: Pair<String, Pair<Float, Float>>? = null,
        val triggerUpdate: Pair<String, Float>? = null
    )
    
    /**
     * Process touch event and return results
     */
    fun processTouchEvent(event: MotionEvent): List<TouchResult> {
        val results = mutableListOf<TouchResult>()
        
        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val result = handlePointerDown(
                    pointerId,
                    event.getX(pointerIndex),
                    event.getY(pointerIndex)
                )
                result?.let { results.add(it) }
            }
            
            MotionEvent.ACTION_MOVE -> {
                // Handle all active pointers
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    val x = event.getX(i)
                    val y = event.getY(i)
                    
                    val result = handlePointerMove(id, x, y)
                    result?.let { results.add(it) }
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val result = handlePointerUp(pointerId)
                result?.let { results.add(it) }
            }
        }
        
        return results
    }
    
    private fun handlePointerDown(pointerId: Int, x: Float, y: Float): TouchResult? {
        // Check buttons first
        val buttonPressed = detectButtonPress(x, y)
        if (buttonPressed != null) {
            pressedButtons.add(buttonPressed)
            return TouchResult(buttonPress = buttonPressed)
        }
        
        // Check analog sticks
        val stickName = detectStickPress(x, y)
        if (stickName != null) {
            activeSticksMap[pointerId] = stickName
            val offset = calculateStickOffset(stickName, x, y)
            updateStickPosition(stickName, offset.first, offset.second)
            return TouchResult(stickUpdate = Pair(stickName, offset))
        }
        
        return null
    }
    
    private fun handlePointerMove(pointerId: Int, x: Float, y: Float): TouchResult? {
        val stickName = activeSticksMap[pointerId]
        if (stickName != null) {
            val offset = calculateStickOffset(stickName, x, y)
            updateStickPosition(stickName, offset.first, offset.second)
            return TouchResult(stickUpdate = Pair(stickName, offset))
        }
        return null
    }
    
    private fun handlePointerUp(pointerId: Int): TouchResult? {
        // Release stick
        val stickName = activeSticksMap.remove(pointerId)
        if (stickName != null) {
            updateStickPosition(stickName, 0f, 0f)
            return TouchResult(stickUpdate = Pair(stickName, Pair(0f, 0f)))
        }
        
        // Release button
        val buttonName = pressedButtons.find { buttonName ->
            // Simple check: if no other pointer on this button
            val buttonLayout = getButtonLayout(buttonName) ?: return@find false
            true  // Button is released when any pointer leaves it
        }
        
        if (buttonName != null) {
            pressedButtons.remove(buttonName)
            return TouchResult(buttonRelease = buttonName)
        }
        
        return null
    }
    
    private fun detectButtonPress(x: Float, y: Float): String? {
        // Check each button in order
        return when {
            isInButton(x, y, layout.buttonA) -> "A"
            isInButton(x, y, layout.buttonB) -> "B"
            isInButton(x, y, layout.buttonX) -> "X"
            isInButton(x, y, layout.buttonY) -> "Y"
            isInButton(x, y, layout.buttonLB) -> "LB"
            isInButton(x, y, layout.buttonRB) -> "RB"
            isInButton(x, y, layout.buttonMenu) -> "Menu"
            isInButton(x, y, layout.buttonView) -> "View"
            isInButton(x, y, layout.buttonXbox) -> "Xbox"
            isInButton(x, y, layout.dpadUp) -> "DPad_Up"
            isInButton(x, y, layout.dpadDown) -> "DPad_Down"
            isInButton(x, y, layout.dpadLeft) -> "DPad_Left"
            isInButton(x, y, layout.dpadRight) -> "DPad_Right"
            else -> null
        }
    }
    
    private fun detectStickPress(x: Float, y: Float): String? {
        return when {
            isInStick(x, y, layout.leftStick) -> "Left"
            isInStick(x, y, layout.rightStick) -> "Right"
            else -> null
        }
    }
    
    private fun isInButton(x: Float, y: Float, button: ButtonLayout): Boolean {
        val actualX = button.x * containerWidth
        val actualY = button.y * containerHeight
        val halfWidth = button.width / 2
        val halfHeight = button.height / 2
        
        return x in (actualX - halfWidth)..(actualX + halfWidth) &&
               y in (actualY - halfHeight)..(actualY + halfHeight)
    }
    
    private fun isInStick(x: Float, y: Float, stick: ButtonLayout): Boolean {
        val centerX = stick.x * containerWidth
        val centerY = stick.y * containerHeight
        val radius = stick.width / 2
        
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt(dx * dx + dy * dy)
        
        return distance <= radius
    }
    
    private fun calculateStickOffset(stickName: String, x: Float, y: Float): Pair<Float, Float> {
        val stick = when (stickName) {
            "Left" -> layout.leftStick
            "Right" -> layout.rightStick
            else -> return Pair(0f, 0f)
        }
        
        val centerX = stick.x * containerWidth
        val centerY = stick.y * containerHeight
        val radius = stick.width / 2
        
        val deltaX = x - centerX
        val deltaY = y - centerY
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
        
        if (distance == 0f) return Pair(0f, 0f)
        
        val normalizedX = deltaX / distance
        val normalizedY = deltaY / distance
        
        // Clamp to radius
        val scale = (distance / radius).coerceIn(0f, 1f)
        
        var finalX = normalizedX * scale
        var finalY = normalizedY * scale
        
        // Apply deadzone
        val deadzone = settings.deadzone
        if (sqrt(finalX * finalX + finalY * finalY) < deadzone) {
            return Pair(0f, 0f)
        }
        
        // Remove deadzone from output
        val magnitude = sqrt(finalX * finalX + finalY * finalY)
        if (magnitude > deadzone) {
            val scale2 = (magnitude - deadzone) / (1f - deadzone)
            finalX = (finalX / magnitude) * scale2
            finalY = (finalY / magnitude) * scale2
        }
        
        // Apply sensitivity
        finalX *= settings.sensitivity
        finalY *= settings.sensitivity
        finalX = finalX.coerceIn(-1f, 1f)
        finalY = finalY.coerceIn(-1f, 1f)
        
        // Apply invert
        if (settings.invertX) finalX = -finalX
        if (settings.invertY) finalY = -finalY
        
        return Pair(finalX, finalY)
    }
    
    private fun updateStickPosition(stickName: String, x: Float, y: Float) {
        when (stickName) {
            "Left" -> {
                leftStickX = x
                leftStickY = y
            }
            "Right" -> {
                rightStickX = x
                rightStickY = y
            }
        }
    }
    
    private fun getButtonLayout(buttonName: String): ButtonLayout? {
        return when (buttonName) {
            "A" -> layout.buttonA
            "B" -> layout.buttonB
            "X" -> layout.buttonX
            "Y" -> layout.buttonY
            "LB" -> layout.buttonLB
            "RB" -> layout.buttonRB
            "Menu" -> layout.buttonMenu
            "View" -> layout.buttonView
            "Xbox" -> layout.buttonXbox
            "DPad_Up" -> layout.dpadUp
            "DPad_Down" -> layout.dpadDown
            "DPad_Left" -> layout.dpadLeft
            "DPad_Right" -> layout.dpadRight
            else -> null
        }
    }
    
    fun getPressedButtons(): Set<String> = pressedButtons.toSet()
    
    fun getLeftStick(): Pair<Float, Float> = Pair(leftStickX, leftStickY)
    
    fun getRightStick(): Pair<Float, Float> = Pair(rightStickX, rightStickY)
}
