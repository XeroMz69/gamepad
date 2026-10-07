package com.gamepad.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Renders a circular button with normalized position and size
 */
@Composable
fun CircularButton(
    x: Float,  // Normalized 0-1
    y: Float,
    size: Dp,
    label: String,
    pressed: Boolean = false,
    opacity: Float = 0.85f,
    onClick: () -> Unit = {},
    onRelease: () -> Unit = {},
    containerWidth: Dp,
    containerHeight: Dp
) {
    val xPx = (x * containerWidth.value).dp
    val yPx = (y * containerHeight.value).dp
    
    Box(
        modifier = Modifier
            .offset(xPx - (size / 2), yPx - (size / 2))
            .clip(CircleShape)
            .background(
                color = if (pressed) Color(0xFF03DAC6) else Color(0xFF333333),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (pressed) Color(0xFFFFFFFF) else Color(0xFF666666),
                shape = CircleShape
            )
    ) {
        // Button content area
    }
}

/**
 * Renders a rectangular button (for D-Pad, LB, RB, etc)
 */
@Composable
fun RectangularButton(
    x: Float,  // Normalized 0-1
    y: Float,
    width: Dp,
    height: Dp,
    label: String,
    pressed: Boolean = false,
    opacity: Float = 0.85f,
    onClick: () -> Unit = {},
    containerWidth: Dp,
    containerHeight: Dp
) {
    val xPx = (x * containerWidth.value).dp
    val yPx = (y * containerHeight.value).dp
    
    Box(
        modifier = Modifier
            .offset(xPx - (width / 2), yPx - (height / 2))
            .background(
                color = if (pressed) Color(0xFF03DAC6) else Color(0xFF444444),
            )
            .border(
                width = 2.dp,
                color = if (pressed) Color(0xFFFFFFFF) else Color(0xFF666666),
            )
    ) {
        // Button content
    }
}

/**
 * Renders an analog stick
 */
@Composable
fun AnalogStick(
    x: Float,  // Normalized 0-1 (center position)
    y: Float,
    size: Dp,
    stickOffsetX: Float = 0f,  // Current stick position X (-1 to 1)
    stickOffsetY: Float = 0f,  // Current stick position Y
    opacity: Float = 0.85f,
    containerWidth: Dp,
    containerHeight: Dp
) {
    val xPx = (x * containerWidth.value).dp
    val yPx = (y * containerHeight.value).dp
    
    val stickRadius = size / 2
    val innerStickRadius = stickRadius * 0.35f
    
    Box(
        modifier = Modifier
            .offset(xPx - stickRadius, yPx - stickRadius)
            .clip(CircleShape)
            .background(Color(0xFF222222))
            .border(2.dp, Color(0xFF666666), CircleShape)
    ) {
        // Outer ring (stationary)
    }
    
    // Inner stick knob (moves with input)
    val knobOffsetX = (stickOffsetX * (stickRadius.value - innerStickRadius.value)).dp
    val knobOffsetY = (stickOffsetY * (stickRadius.value - innerStickRadius.value)).dp
    
    Box(
        modifier = Modifier
            .offset(
                xPx - innerStickRadius + knobOffsetX,
                yPx - innerStickRadius + knobOffsetY
            )
            .clip(CircleShape)
            .background(Color(0xFF03DAC6))
            .border(1.dp, Color(0xFFFFFFFF), CircleShape)
    ) {
        // Inner knob
    }
}

/**
 * Renders a trigger (LT/RT) with analog visual representation
 */
@Composable
fun TriggerButton(
    x: Float,
    y: Float,
    width: Dp,
    height: Dp,
    label: String,
    value: Float = 0f,  // 0 to 1
    pressed: Boolean = false,
    containerWidth: Dp,
    containerHeight: Dp
) {
    val xPx = (x * containerWidth.value).dp
    val yPx = (y * containerHeight.value).dp
    
    Box(
        modifier = Modifier
            .offset(xPx - (width / 2), yPx - (height / 2))
            .background(
                color = if (value > 0.1f) Color(0xFF03DAC6) else Color(0xFF444444)
            )
            .border(
                width = 2.dp,
                color = if (value > 0.1f) Color(0xFFFFFFFF) else Color(0xFF666666)
            )
    ) {
        // Trigger visual with value indication
    }
}

/**
 * Renders the D-Pad as a cross pattern
 */
@Composable
fun DPad(
    centerX: Float,
    centerY: Float,
    buttonSize: Dp,
    spacing: Dp = 2.dp,
    upPressed: Boolean = false,
    downPressed: Boolean = false,
    leftPressed: Boolean = false,
    rightPressed: Boolean = false,
    containerWidth: Dp,
    containerHeight: Dp
) {
    val offset = (buttonSize + spacing) / 2
    
    // Up
    RectangularButton(
        x = centerX,
        y = centerY - offset.value / containerHeight.value,
        width = buttonSize,
        height = buttonSize,
        label = "↑",
        pressed = upPressed,
        containerWidth = containerWidth,
        containerHeight = containerHeight
    )
    
    // Down
    RectangularButton(
        x = centerX,
        y = centerY + offset.value / containerHeight.value,
        width = buttonSize,
        height = buttonSize,
        label = "↓",
        pressed = downPressed,
        containerWidth = containerWidth,
        containerHeight = containerHeight
    )
    
    // Left
    RectangularButton(
        x = centerX - offset.value / containerWidth.value,
        y = centerY,
        width = buttonSize,
        height = buttonSize,
        label = "←",
        pressed = leftPressed,
        containerWidth = containerWidth,
        containerHeight = containerHeight
    )
    
    // Right
    RectangularButton(
        x = centerX + offset.value / containerWidth.value,
        y = centerY,
        width = buttonSize,
        height = buttonSize,
        label = "→",
        pressed = rightPressed,
        containerWidth = containerWidth,
        containerHeight = containerHeight
    )
}

/**
 * Xbox button (home button in center)
 */
@Composable
fun XboxButton(
    x: Float,
    y: Float,
    size: Dp,
    pressed: Boolean = false,
    containerWidth: Dp,
    containerHeight: Dp
) {
    val xPx = (x * containerWidth.value).dp
    val yPx = (y * containerHeight.value).dp
    
    Box(
        modifier = Modifier
            .offset(xPx - (size / 2), yPx - (size / 2))
            .clip(CircleShape)
            .background(
                color = if (pressed) Color(0xFF03DAC6) else Color(0xFF333333),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (pressed) Color(0xFFFFFFFF) else Color(0xFF666666),
                shape = CircleShape
            )
    ) {
        // Xbox logo area
    }
}
