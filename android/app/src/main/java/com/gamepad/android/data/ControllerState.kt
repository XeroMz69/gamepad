package com.gamepad.android.data

data class ControllerState(
    // Digital buttons
    val buttonA: Boolean = false,
    val buttonB: Boolean = false,
    val buttonX: Boolean = false,
    val buttonY: Boolean = false,
    val buttonLB: Boolean = false,
    val buttonRB: Boolean = false,
    val buttonMenu: Boolean = false,
    val buttonView: Boolean = false,
    val buttonXbox: Boolean = false,
    
    // D-Pad
    val dpadUp: Boolean = false,
    val dpadDown: Boolean = false,
    val dpadLeft: Boolean = false,
    val dpadRight: Boolean = false,
    
    // Analog sticks (-1.0 to 1.0)
    val leftStickX: Float = 0f,
    val leftStickY: Float = 0f,
    val rightStickX: Float = 0f,
    val rightStickY: Float = 0f,
    
    // Triggers (0.0 to 1.0)
    val triggerLT: Float = 0f,
    val triggerRT: Float = 0f
) {
    fun toByteArray(): ByteArray {
        // 22 bytes total packet
        val bytes = ByteArray(22)
        var offset = 0
        
        // Sequence number placeholder (will be filled by network layer) - 4 bytes
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        
        // Timestamp placeholder - 8 bytes
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        bytes[offset++] = 0
        
        // Button bitmask - 2 bytes
        var buttons = 0
        if (buttonA) buttons = buttons or (1 shl 0)
        if (buttonB) buttons = buttons or (1 shl 1)
        if (buttonX) buttons = buttons or (1 shl 2)
        if (buttonY) buttons = buttons or (1 shl 3)
        if (buttonLB) buttons = buttons or (1 shl 4)
        if (buttonRB) buttons = buttons or (1 shl 5)
        if (dpadUp) buttons = buttons or (1 shl 6)
        if (dpadDown) buttons = buttons or (1 shl 7)
        if (dpadLeft) buttons = buttons or (1 shl 8)
        if (dpadRight) buttons = buttons or (1 shl 9)
        if (buttonMenu) buttons = buttons or (1 shl 10)
        if (buttonView) buttons = buttons or (1 shl 11)
        if (buttonXbox) buttons = buttons or (1 shl 12)
        
        bytes[offset++] = (buttons shr 8).toByte()
        bytes[offset++] = buttons.toByte()
        
        // Analog sticks - 8 bytes (4x int16)
        val lx = (leftStickX * 32767).toInt().toShort()
        val ly = (leftStickY * 32767).toInt().toShort()
        val rx = (rightStickX * 32767).toInt().toShort()
        val ry = (rightStickY * 32767).toInt().toShort()
        
        bytes[offset++] = (lx.toInt() shr 8).toByte()
        bytes[offset++] = lx.toByte()
        bytes[offset++] = (ly.toInt() shr 8).toByte()
        bytes[offset++] = ly.toByte()
        bytes[offset++] = (rx.toInt() shr 8).toByte()
        bytes[offset++] = rx.toByte()
        bytes[offset++] = (ry.toInt() shr 8).toByte()
        bytes[offset++] = ry.toByte()
        
        // Triggers - 2 bytes
        val lt = (triggerLT * 255).toInt().toByte()
        val rt = (triggerRT * 255).toInt().toByte()
        bytes[offset++] = lt
        bytes[offset++] = rt
        
        return bytes
    }
}
