package com.gamepad.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.gamepad.android.data.ControllerLayout
import com.gamepad.android.data.ControllerSettings
import com.gamepad.android.input.TouchInputHandler
import com.gamepad.android.ui.screens.ControllerScreen
import com.gamepad.android.viewmodel.GamepadViewModel

/**
 * Interactive controller surface that captures touch events and updates controller state
 */
@Composable
fun InteractiveControllerSurface(
    viewModel: GamepadViewModel,
    layout: ControllerLayout,
    settings: ControllerSettings
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    val containerWidthPx = with(density) { screenWidth.toPx() }
    val containerHeightPx = with(density) { screenHeight.toPx() }
    
    val touchHandler = remember(layout, settings) {
        TouchInputHandler(containerWidthPx, containerHeightPx, layout, settings)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .pointerInput(Unit) {
                // This will capture raw touch events
                // We need a custom approach for multi-touch handling
            }
    ) {
        ControllerScreen(viewModel)
    }
}

/**
 * Enhanced controller screen with touch interactivity
 */
@Composable
fun TouchResponsiveControllerScreen(viewModel: GamepadViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    val layout by viewModel.currentLayout.collectAsState()
    val settings by viewModel.controllerSettings.collectAsState()
    val controllerState by viewModel.controllerState.collectAsState()
    
    val containerWidthPx = with(density) { screenWidth.toPx() }
    val containerHeightPx = with(density) { screenHeight.toPx() }
    
    val touchHandler = remember(layout, settings) {
        TouchInputHandler(containerWidthPx, containerHeightPx, layout, settings)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        // Handle button press
                        val buttonPressed = detectButtonAtOffset(offset.x, offset.y, layout, containerWidthPx, containerHeightPx)
                        if (buttonPressed != null) {
                            viewModel.setButtonState(buttonPressed, true)
                        }
                        
                        // Wait for release
                        tryAwaitRelease()
                        
                        if (buttonPressed != null) {
                            viewModel.setButtonState(buttonPressed, false)
                        }
                    }
                )
            }
    ) {
        ControllerScreen(viewModel)
    }
}

/**
 * Detect which button is at the given offset coordinates
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
        else -> null
    }
}

/**
 * Custom canvas-based controller with gesture handling
 * This provides better multi-touch support than standard Compose gestures
 */
@Composable
fun GestureControllerScreen(viewModel: GamepadViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    val layout by viewModel.currentLayout.collectAsState()
    val settings by viewModel.controllerSettings.collectAsState()
    
    val containerWidthPx = with(density) { screenWidth.toPx() }
    val containerHeightPx = with(density) { screenHeight.toPx() }
    
    val touchHandler = remember(layout, settings) {
        TouchInputHandler(containerWidthPx, containerHeightPx, layout, settings)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .pointerInput(Unit) {
                // For now, basic tap gesture detection
                // Full multi-touch support would require custom touch event handling
                detectTapGestures(
                    onPress = { offset ->
                        val buttonName = detectButtonAtOffset(offset.x, offset.y, layout, containerWidthPx, containerHeightPx)
                        if (buttonName != null) {
                            viewModel.setButtonState(buttonName, true)
                            viewModel.addDebugLog("Button pressed: $buttonName")
                        }
                        
                        tryAwaitRelease()
                        
                        if (buttonName != null) {
                            viewModel.setButtonState(buttonName, false)
                            viewModel.addDebugLog("Button released: $buttonName")
                        }
                    }
                )
            }
    ) {
        ControllerScreen(viewModel)
    }
}
