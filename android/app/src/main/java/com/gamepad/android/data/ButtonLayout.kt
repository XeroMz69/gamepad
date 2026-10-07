package com.gamepad.android.data

import kotlinx.serialization.Serializable

@Serializable
data class ButtonLayout(
    val x: Float,  // Normalized 0-1
    val y: Float,  // Normalized 0-1
    val width: Float,
    val height: Float,
    val opacity: Float = 0.85f
)

@Serializable
data class ControllerLayout(
    val profileName: String = "Default",
    val buttonA: ButtonLayout = ButtonLayout(0.82f, 0.72f, 72f, 72f),
    val buttonB: ButtonLayout = ButtonLayout(0.92f, 0.62f, 72f, 72f),
    val buttonX: ButtonLayout = ButtonLayout(0.72f, 0.62f, 72f, 72f),
    val buttonY: ButtonLayout = ButtonLayout(0.82f, 0.52f, 72f, 72f),
    
    val buttonLB: ButtonLayout = ButtonLayout(0.15f, 0.15f, 80f, 40f),
    val buttonRB: ButtonLayout = ButtonLayout(0.85f, 0.15f, 80f, 40f),
    
    val triggerLT: ButtonLayout = ButtonLayout(0.20f, 0.08f, 60f, 40f),
    val triggerRT: ButtonLayout = ButtonLayout(0.80f, 0.08f, 60f, 40f),
    
    val dpadUp: ButtonLayout = ButtonLayout(0.12f, 0.55f, 50f, 50f),
    val dpadDown: ButtonLayout = ButtonLayout(0.12f, 0.70f, 50f, 50f),
    val dpadLeft: ButtonLayout = ButtonLayout(0.05f, 0.62f, 50f, 50f),
    val dpadRight: ButtonLayout = ButtonLayout(0.19f, 0.62f, 50f, 50f),
    
    val leftStick: ButtonLayout = ButtonLayout(0.20f, 0.50f, 100f, 100f),
    val rightStick: ButtonLayout = ButtonLayout(0.80f, 0.50f, 100f, 100f),
    
    val buttonMenu: ButtonLayout = ButtonLayout(0.42f, 0.48f, 50f, 40f),
    val buttonView: ButtonLayout = ButtonLayout(0.58f, 0.48f, 50f, 40f),
    val buttonXbox: ButtonLayout = ButtonLayout(0.50f, 0.38f, 60f, 60f)
)

fun getDefaultLayout(): ControllerLayout = ControllerLayout(profileName = "Default Xbox")
