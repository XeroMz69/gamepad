package com.gamepad.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gamepad.android.data.ControllerLayout
import com.gamepad.android.ui.components.AnalogStick
import com.gamepad.android.ui.components.CircularButton
import com.gamepad.android.ui.components.DPad
import com.gamepad.android.ui.components.RectangularButton
import com.gamepad.android.ui.components.TriggerButton
import com.gamepad.android.ui.components.XboxButton
import com.gamepad.android.viewmodel.GamepadViewModel
import kotlin.math.sqrt

/**
 * Main controller screen displaying Xbox layout with all buttons and sticks
 */
@Composable
fun ControllerScreen(viewModel: GamepadViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    val layout by viewModel.currentLayout.collectAsState()
    val controllerState by viewModel.controllerState.collectAsState()
    val settings by viewModel.controllerSettings.collectAsState()
    
    val containerWidthPx = with(density) { screenWidth.toPx() }
    val containerHeightPx = with(density) { screenHeight.toPx() }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val buttonName = detectButtonAtOffset(
                            offset.x, offset.y, layout, containerWidthPx, containerHeightPx
                        )
                        if (buttonName != null && !isStickButton(buttonName)) {
                            viewModel.setButtonState(buttonName, true)
                        }
                        tryAwaitRelease()
                        if (buttonName != null && !isStickButton(buttonName)) {
                            viewModel.setButtonState(buttonName, false)
                        }
                    }
                )
            }
    ) {
        // Container for all controller elements
        Box(
            modifier = Modifier
                .size(screenWidth, screenHeight)
                .background(Color(0xFF0A0A0A))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Check if drag started on a stick
                            val stickName = detectStickAtOffset(offset.x, offset.y, layout, containerWidthPx, containerHeightPx)
                            if (stickName != null) {
                                // Stick drag will be handled in onDrag
                            }
                        },
                        onDrag = { change, dragAmount ->
                            val offset = change.position
                            val stickName = detectStickAtOffset(offset.x, offset.y, layout, containerWidthPx, containerHeightPx)
                            
                            if (stickName != null) {
                                val (x, y) = calculateStickOffset(stickName, offset.x, offset.y, layout, containerWidthPx, containerHeightPx, settings)
                                viewModel.setAnalogStick(stickName, x, y)
                            }
                        },
                        onDragEnd = {
                            // Reset sticks to center
                            viewModel.setAnalogStick("Left", 0f, 0f)
                            viewModel.setAnalogStick("Right", 0f, 0f)
                        }
                    )
                }
        ) {
            // Left side: D-Pad
            DPad(
                centerX = layout.dpadUp.x,
                centerY = layout.dpadUp.y,
                buttonSize = 50.dp,
                upPressed = controllerState.dpadUp,
                downPressed = controllerState.dpadDown,
                leftPressed = controllerState.dpadLeft,
                rightPressed = controllerState.dpadRight,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Left side: Left Analog Stick
            AnalogStick(
                x = layout.leftStick.x,
                y = layout.leftStick.y,
                size = layout.leftStick.width.dp,
                stickOffsetX = controllerState.leftStickX,
                stickOffsetY = controllerState.leftStickY,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Right side: Face Buttons (A, B, X, Y)
            CircularButton(
                x = layout.buttonA.x,
                y = layout.buttonA.y,
                size = layout.buttonA.width.dp,
                label = "A",
                pressed = controllerState.buttonA,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            CircularButton(
                x = layout.buttonB.x,
                y = layout.buttonB.y,
                size = layout.buttonB.width.dp,
                label = "B",
                pressed = controllerState.buttonB,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            CircularButton(
                x = layout.buttonX.x,
                y = layout.buttonX.y,
                size = layout.buttonX.width.dp,
                label = "X",
                pressed = controllerState.buttonX,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            CircularButton(
                x = layout.buttonY.x,
                y = layout.buttonY.y,
                size = layout.buttonY.width.dp,
                label = "Y",
                pressed = controllerState.buttonY,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Right side: Right Analog Stick
            AnalogStick(
                x = layout.rightStick.x,
                y = layout.rightStick.y,
                size = layout.rightStick.width.dp,
                stickOffsetX = controllerState.rightStickX,
                stickOffsetY = controllerState.rightStickY,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Shoulder buttons: LB, RB
            RectangularButton(
                x = layout.buttonLB.x,
                y = layout.buttonLB.y,
                width = layout.buttonLB.width.dp,
                height = layout.buttonLB.height.dp,
                label = "LB",
                pressed = controllerState.buttonLB,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            RectangularButton(
                x = layout.buttonRB.x,
                y = layout.buttonRB.y,
                width = layout.buttonRB.width.dp,
                height = layout.buttonRB.height.dp,
                label = "RB",
                pressed = controllerState.buttonRB,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Triggers: LT, RT
            TriggerButton(
                x = layout.triggerLT.x,
                y = layout.triggerLT.y,
                width = layout.triggerLT.width.dp,
                height = layout.triggerLT.height.dp,
                label = "LT",
                value = controllerState.triggerLT,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            TriggerButton(
                x = layout.triggerRT.x,
                y = layout.triggerRT.y,
                width = layout.triggerRT.width.dp,
                height = layout.triggerRT.height.dp,
                label = "RT",
                value = controllerState.triggerRT,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            // Center buttons: Menu, View, Xbox
            RectangularButton(
                x = layout.buttonMenu.x,
                y = layout.buttonMenu.y,
                width = layout.buttonMenu.width.dp,
                height = layout.buttonMenu.height.dp,
                label = "Menu",
                pressed = controllerState.buttonMenu,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            RectangularButton(
                x = layout.buttonView.x,
                y = layout.buttonView.y,
                width = layout.buttonView.width.dp,
                height = layout.buttonView.height.dp,
                label = "View",
                pressed = controllerState.buttonView,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
            
            XboxButton(
                x = layout.buttonXbox.x,
                y = layout.buttonXbox.y,
                size = layout.buttonXbox.width.dp,
                pressed = controllerState.buttonXbox,
                containerWidth = screenWidth,
                containerHeight = screenHeight
            )
        }
    }
}

/**
 * Detect which button/stick is at the given offset coordinates
 */
private fun detectButtonAtOffset(
    x: Float,
    y: Float,
    layout: ControllerLayout,
    containerWidth: Float,
    containerHeight: Float
): String? {
    fun isInButton(buttonLayout: com.gamepad.android.data.ButtonLayout, px: Float, py: Float): Boolean {
        val actualX = buttonLayout.x * containerWidth
        val actualY = buttonLayout.y * containerHeight
        val halfWidth = buttonLayout.width / 2
        val halfHeight = buttonLayout.height / 2
        
        return px in (actualX - halfWidth)..(actualX + halfWidth) &&
               py in (actualY - halfHeight)..(actualY + halfHeight)
    }
    
    return when {
        isInButton(layout.buttonA, x, y) -> "A"
        isInButton(layout.buttonB, x, y) -> "B"
        isInButton(layout.buttonX, x, y) -> "X"
        isInButton(layout.buttonY, x, y) -> "Y"
        isInButton(layout.buttonLB, x, y) -> "LB"
        isInButton(layout.buttonRB, x, y) -> "RB"
        isInButton(layout.buttonMenu, x, y) -> "Menu"
        isInButton(layout.buttonView, x, y) -> "View"
        isInButton(layout.buttonXbox, x, y) -> "Xbox"
        isInButton(layout.dpadUp, x, y) -> "DPad_Up"
        isInButton(layout.dpadDown, x, y) -> "DPad_Down"
        isInButton(layout.dpadLeft, x, y) -> "DPad_Left"
        isInButton(layout.dpadRight, x, y) -> "DPad_Right"
        isInButton(layout.triggerLT, x, y) -> "LT"
        isInButton(layout.triggerRT, x, y) -> "RT"
        else -> null
    }
}

/**
 * Detect which stick is at the given offset coordinates
 */
private fun detectStickAtOffset(
    x: Float,
    y: Float,
    layout: ControllerLayout,
    containerWidth: Float,
    containerHeight: Float
): String? {
    fun isInStick(stickLayout: com.gamepad.android.data.ButtonLayout, px: Float, py: Float): Boolean {
        val centerX = stickLayout.x * containerWidth
        val centerY = stickLayout.y * containerHeight
        val radius = stickLayout.width / 2
        
        val dx = px - centerX
        val dy = py - centerY
        val distance = sqrt(dx * dx + dy * dy)
        
        return distance <= radius * 1.5f  // Allow detection outside circle for ease
    }
    
    return when {
        isInStick(layout.leftStick, x, y) -> "Left"
        isInStick(layout.rightStick, x, y) -> "Right"
        else -> null
    }
}

/**
 * Calculate stick offset from touch point
 */
private fun calculateStickOffset(
    stickName: String,
    x: Float,
    y: Float,
    layout: ControllerLayout,
    containerWidth: Float,
    containerHeight: Float,
    settings: com.gamepad.android.data.ControllerSettings
): Pair<Float, Float> {
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
    val magnitude = sqrt(finalX * finalX + finalY * finalY)
    
    if (magnitude < deadzone) {
        return Pair(0f, 0f)
    }
    
    // Remove deadzone from output
    val scaledMagnitude = (magnitude - deadzone) / (1f - deadzone)
    finalX = (finalX / magnitude) * scaledMagnitude
    finalY = (finalY / magnitude) * scaledMagnitude
    
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

/**
 * Check if a button name is a stick (not a regular button)
 */
private fun isStickButton(buttonName: String): Boolean {
    return buttonName in setOf("Left", "Right", "LT", "RT")
}
