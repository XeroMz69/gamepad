package com.gamepad.android.ui.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gamepad.android.data.ButtonLayout
import com.gamepad.android.data.ControllerLayout

/**
 * Helper class for responsive layout calculations
 * Converts normalized coordinates (0-1) to actual pixel values
 */
object ResponsiveLayout {
    
    /**
     * Calculate actual X position in pixels from normalized value
     */
    fun calculateXPos(normalized: Float, containerWidth: Dp): Dp {
        return (normalized * containerWidth.value).dp
    }
    
    /**
     * Calculate actual Y position in pixels from normalized value
     */
    fun calculateYPos(normalized: Float, containerHeight: Dp): Dp {
        return (normalized * containerHeight.value).dp
    }
    
    /**
     * Scale a layout to fit a specific container size
     */
    fun scaleLayout(
        layout: ControllerLayout,
        containerWidth: Float,
        containerHeight: Float,
        referenceWidth: Float = 1080f,  // Reference size for default layout
        referenceHeight: Float = 2340f
    ): ControllerLayout {
        val scaleX = containerWidth / referenceWidth
        val scaleY = containerHeight / referenceHeight
        
        return ControllerLayout(
            profileName = layout.profileName,
            buttonA = layout.buttonA.scale(scaleX, scaleY),
            buttonB = layout.buttonB.scale(scaleX, scaleY),
            buttonX = layout.buttonX.scale(scaleX, scaleY),
            buttonY = layout.buttonY.scale(scaleX, scaleY),
            buttonLB = layout.buttonLB.scale(scaleX, scaleY),
            buttonRB = layout.buttonRB.scale(scaleX, scaleY),
            triggerLT = layout.triggerLT.scale(scaleX, scaleY),
            triggerRT = layout.triggerRT.scale(scaleX, scaleY),
            dpadUp = layout.dpadUp.scale(scaleX, scaleY),
            dpadDown = layout.dpadDown.scale(scaleX, scaleY),
            dpadLeft = layout.dpadLeft.scale(scaleX, scaleY),
            dpadRight = layout.dpadRight.scale(scaleX, scaleY),
            leftStick = layout.leftStick.scale(scaleX, scaleY),
            rightStick = layout.rightStick.scale(scaleX, scaleY),
            buttonMenu = layout.buttonMenu.scale(scaleX, scaleY),
            buttonView = layout.buttonView.scale(scaleX, scaleY),
            buttonXbox = layout.buttonXbox.scale(scaleX, scaleY)
        )
    }
    
    /**
     * Check if a touch point is within a button's bounds
     */
    fun isTouchInButton(
        touchX: Float,
        touchY: Float,
        buttonX: Float,
        buttonY: Float,
        buttonWidth: Dp,
        buttonHeight: Dp,
        containerWidth: Dp,
        containerHeight: Dp
    ): Boolean {
        val actualX = buttonX * containerWidth.value
        val actualY = buttonY * containerHeight.value
        val actualWidth = buttonWidth.value
        val actualHeight = buttonHeight.value
        
        return touchX in (actualX - actualWidth / 2)..(actualX + actualWidth / 2) &&
               touchY in (actualY - actualHeight / 2)..(actualY + actualHeight / 2)
    }
    
    /**
     * Calculate stick offset from touch point
     */
    fun calculateStickOffset(
        touchX: Float,
        touchY: Float,
        stickCenterX: Float,
        stickCenterY: Float,
        stickRadius: Dp,
        containerWidth: Dp,
        containerHeight: Dp
    ): Pair<Float, Float> {
        val actualCenterX = stickCenterX * containerWidth.value
        val actualCenterY = stickCenterY * containerHeight.value
        val actualRadius = stickRadius.value
        
        val deltaX = touchX - actualCenterX
        val deltaY = touchY - actualCenterY
        
        val distance = kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
        
        return if (distance > 0) {
            val normalizedX = (deltaX / distance).coerceIn(-1f, 1f)
            val normalizedY = (deltaY / distance).coerceIn(-1f, 1f)
            
            // Scale by distance, but cap at radius
            val scale = (distance / actualRadius).coerceIn(0f, 1f)
            Pair(normalizedX * scale, normalizedY * scale)
        } else {
            Pair(0f, 0f)
        }
    }
}

/**
 * Extension function to scale a ButtonLayout
 */
fun ButtonLayout.scale(scaleX: Float, scaleY: Float): ButtonLayout {
    return ButtonLayout(
        x = this.x * scaleX,
        y = this.y * scaleY,
        width = this.width * scaleX,
        height = this.height * scaleY,
        opacity = this.opacity
    )
}
