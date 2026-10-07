package com.gamepad.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamepad.android.data.ControllerLayout
import com.gamepad.android.data.ControllerSettings
import com.gamepad.android.data.ControllerState
import com.gamepad.android.data.ConnectionSettings
import com.gamepad.android.data.ConnectionStatus
import com.gamepad.android.data.ResponseCurve
import com.gamepad.android.data.applyResponseCurve
import com.gamepad.android.data.getDefaultLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamepadViewModel : ViewModel() {
    
    // Controller state
    private val _controllerState = MutableStateFlow(ControllerState())
    val controllerState: StateFlow<ControllerState> = _controllerState.asStateFlow()
    
    // Layout state
    private val _currentLayout = MutableStateFlow(getDefaultLayout())
    val currentLayout: StateFlow<ControllerLayout> = _currentLayout.asStateFlow()
    
    // Connection state
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()
    
    private val _latency = MutableStateFlow(0)
    val latency: StateFlow<Int> = _latency.asStateFlow()
    
    private val _packetLoss = MutableStateFlow(0f)
    val packetLoss: StateFlow<Float> = _packetLoss.asStateFlow()
    
    // Connection settings
    private val _connectionSettings = MutableStateFlow(ConnectionSettings())
    val connectionSettings: StateFlow<ConnectionSettings> = _connectionSettings.asStateFlow()
    
    // Controller settings
    private val _controllerSettings = MutableStateFlow(ControllerSettings())
    val controllerSettings: StateFlow<ControllerSettings> = _controllerSettings.asStateFlow()
    
    // Edit mode state
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()
    
    // Selected button for editing
    private val _selectedButton = MutableStateFlow<String?>(null)
    val selectedButton: StateFlow<String?> = _selectedButton.asStateFlow()
    
    // Available profiles
    private val _profiles = MutableStateFlow(listOf("Default Xbox", "FPS", "Racing"))
    val profiles: StateFlow<List<String>> = _profiles.asStateFlow()
    
    private val _currentProfile = MutableStateFlow("Default Xbox")
    val currentProfile: StateFlow<String> = _currentProfile.asStateFlow()
    
    // Debug logs
    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    val debugLogs: StateFlow<List<String>> = _debugLogs.asStateFlow()
    
    private val _debugMode = MutableStateFlow(false)
    val debugMode: StateFlow<Boolean> = _debugMode.asStateFlow()
    
    // ===== Controller State Updates =====
    
    fun setButtonState(buttonName: String, pressed: Boolean) {
        _controllerState.value = when (buttonName) {
            "A" -> _controllerState.value.copy(buttonA = pressed)
            "B" -> _controllerState.value.copy(buttonB = pressed)
            "X" -> _controllerState.value.copy(buttonX = pressed)
            "Y" -> _controllerState.value.copy(buttonY = pressed)
            "LB" -> _controllerState.value.copy(buttonLB = pressed)
            "RB" -> _controllerState.value.copy(buttonRB = pressed)
            "Menu" -> _controllerState.value.copy(buttonMenu = pressed)
            "View" -> _controllerState.value.copy(buttonView = pressed)
            "Xbox" -> _controllerState.value.copy(buttonXbox = pressed)
            "DPad_Up" -> _controllerState.value.copy(dpadUp = pressed)
            "DPad_Down" -> _controllerState.value.copy(dpadDown = pressed)
            "DPad_Left" -> _controllerState.value.copy(dpadLeft = pressed)
            "DPad_Right" -> _controllerState.value.copy(dpadRight = pressed)
            else -> _controllerState.value
        }
    }
    
    fun setAnalogStick(stickName: String, x: Float, y: Float) {
        val settings = _controllerSettings.value
        
        // Apply deadzone
        val processedX = applyDeadzone(x, settings.deadzone)
        val processedY = applyDeadzone(y, settings.deadzone)
        
        // Apply sensitivity
        val sensitiveX = (processedX * settings.sensitivity).coerceIn(-1f, 1f)
        val sensitiveY = (processedY * settings.sensitivity).coerceIn(-1f, 1f)
        
        // Apply response curve
        val curvedX = applyResponseCurve(sensitiveX, settings.responseCurve)
        val curvedY = applyResponseCurve(sensitiveY, settings.responseCurve)
        
        // Apply invert
        val finalX = if (settings.invertX) -curvedX else curvedX
        val finalY = if (settings.invertY) -curvedY else curvedY
        
        _controllerState.value = when (stickName) {
            "Left" -> _controllerState.value.copy(leftStickX = finalX, leftStickY = finalY)
            "Right" -> _controllerState.value.copy(rightStickX = finalX, rightStickY = finalY)
            else -> _controllerState.value
        }
    }
    
    fun setTrigger(triggerName: String, value: Float) {
        val clampedValue = value.coerceIn(0f, 1f)
        _controllerState.value = when (triggerName) {
            "LT" -> _controllerState.value.copy(triggerLT = clampedValue)
            "RT" -> _controllerState.value.copy(triggerRT = clampedValue)
            else -> _controllerState.value
        }
    }
    
    fun resetControllerState() {
        _controllerState.value = ControllerState()
    }
    
    // ===== Connection Management =====
    
    fun setConnectionStatus(status: ConnectionStatus) {
        _connectionStatus.value = status
    }
    
    fun updateLatency(ms: Int) {
        _latency.value = ms
    }
    
    fun updatePacketLoss(percentage: Float) {
        _packetLoss.value = percentage
    }
    
    fun updateConnectionSettings(settings: ConnectionSettings) {
        _connectionSettings.value = settings
    }
    
    // ===== Layout Management =====
    
    fun updateLayout(layout: ControllerLayout) {
        _currentLayout.value = layout
    }
    
    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }
    
    fun selectButton(buttonName: String?) {
        _selectedButton.value = buttonName
    }
    
    // ===== Controller Settings =====
    
    fun updateControllerSettings(settings: ControllerSettings) {
        _controllerSettings.value = settings
    }
    
    fun setDeadzone(value: Float) {
        _controllerSettings.value = _controllerSettings.value.copy(deadzone = value.coerceIn(0f, 0.5f))
    }
    
    fun setSensitivity(value: Float) {
        _controllerSettings.value = _controllerSettings.value.copy(sensitivity = value.coerceIn(0.5f, 2.0f))
    }
    
    fun setResponseCurve(curve: ResponseCurve) {
        _controllerSettings.value = _controllerSettings.value.copy(responseCurve = curve)
    }
    
    // ===== Profile Management =====
    
    fun setCurrentProfile(profile: String) {
        _currentProfile.value = profile
    }
    
    fun addProfile(name: String) {
        _profiles.value = _profiles.value + name
    }
    
    fun deleteProfile(name: String) {
        _profiles.value = _profiles.value - name
    }
    
    // ===== Debug =====
    
    fun toggleDebugMode() {
        _debugMode.value = !_debugMode.value
    }
    
    fun addDebugLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        val log = "[$timestamp] $message"
        _debugLogs.value = (_debugLogs.value + log).takeLast(100)  // Keep last 100 logs
    }
    
    fun clearDebugLogs() {
        _debugLogs.value = emptyList()
    }
    
    // ===== Utility =====
    
    private fun applyDeadzone(value: Float, deadzone: Float): Float {
        return if (kotlin.math.abs(value) < deadzone) {
            0f
        } else {
            val sign = if (value < 0) -1f else 1f
            sign * (kotlin.math.abs(value) - deadzone) / (1f - deadzone)
        }
    }
}
