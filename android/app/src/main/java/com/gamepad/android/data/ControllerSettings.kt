package com.gamepad.android.data

data class ControllerSettings(
    val deadzone: Float = 0.15f,  // 15%
    val sensitivity: Float = 1.0f,  // 1.0x = normal
    val responseCurve: ResponseCurve = ResponseCurve.LINEAR,
    val invertX: Boolean = false,
    val invertY: Boolean = false,
    val vibrationEnabled: Boolean = true
)

enum class ResponseCurve {
    LINEAR,
    QUADRATIC,
    CUBIC
}

fun applyResponseCurve(value: Float, curve: ResponseCurve): Float {
    return when (curve) {
        ResponseCurve.LINEAR -> value
        ResponseCurve.QUADRATIC -> {
            val sign = if (value < 0) -1f else 1f
            sign * value * value
        }
        ResponseCurve.CUBIC -> {
            val sign = if (value < 0) -1f else 1f
            sign * value * value * value
        }
    }
}
