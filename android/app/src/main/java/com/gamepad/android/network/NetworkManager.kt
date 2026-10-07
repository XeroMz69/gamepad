package com.gamepad.android.network

import android.util.Log
import com.gamepad.android.data.ConnectionStatus
import com.gamepad.android.data.ControllerState
import com.gamepad.android.viewmodel.GamepadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Manages network communication and coordinates with ViewModel
 */
class NetworkManager(
    private val viewModel: GamepadViewModel,
    private val coroutineScope: CoroutineScope
) {
    
    private var udpClient: GamepadUdpClient? = null
    private var sendJob: Job? = null
    
    companion object {
        const val TAG = "NetworkManager"
    }
    
    /**
     * Connect to PC server
     */
    fun connect(serverIp: String, serverPort: Int) {
        viewModel.setConnectionStatus(ConnectionStatus.CONNECTING)
        viewModel.addDebugLog("[Network] Connecting to $serverIp:$serverPort...")
        
        coroutineScope.launch {
            try {
                udpClient = GamepadUdpClient(
                    serverIp = serverIp,
                    serverPort = serverPort,
                    onConnected = {
                        viewModel.setConnectionStatus(ConnectionStatus.CONNECTED)
                        viewModel.addDebugLog("[Network] Connected to server")
                        Log.d(TAG, "Connected to $serverIp:$serverPort")
                        startSendingControllerState()
                    },
                    onDisconnected = {
                        viewModel.setConnectionStatus(ConnectionStatus.DISCONNECTED)
                        viewModel.resetControllerState()
                        viewModel.addDebugLog("[Network] Disconnected from server")
                        Log.d(TAG, "Disconnected from server")
                    },
                    onLatencyUpdate = { latency ->
                        viewModel.updateLatency(latency)
                    },
                    onError = { error ->
                        viewModel.setConnectionStatus(ConnectionStatus.ERROR)
                        viewModel.addDebugLog("[Network] Error: $error")
                        Log.e(TAG, "Network error: $error")
                    }
                )
                
                val connected = udpClient!!.connect()
                if (!connected) {
                    viewModel.setConnectionStatus(ConnectionStatus.ERROR)
                    viewModel.addDebugLog("[Network] Failed to connect")
                }
            } catch (e: Exception) {
                viewModel.setConnectionStatus(ConnectionStatus.ERROR)
                viewModel.addDebugLog("[Network] Exception: ${e.message}")
                Log.e(TAG, "Connection exception: ${e.message}")
            }
        }
    }
    
    /**
     * Disconnect from server
     */
    fun disconnect() {
        sendJob?.cancel()
        sendJob = null
        
        coroutineScope.launch {
            udpClient?.disconnect()
            udpClient = null
            viewModel.resetControllerState()
            viewModel.setConnectionStatus(ConnectionStatus.DISCONNECTED)
            viewModel.addDebugLog("[Network] Manual disconnect")
        }
    }
    
    /**
     * Start sending controller state continuously
     */
    private fun startSendingControllerState() {
        sendJob?.cancel()
        
        sendJob = coroutineScope.launch {
            udpClient?.sendContinuousStream { stateProvider ->
                // Get current controller state from ViewModel
                viewModel.controllerState.value.toByteArray()
            }
        }
        
        viewModel.addDebugLog("[Network] Started sending controller state @ 60Hz")
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean = udpClient?.isConnected() ?: false
    
    /**
     * Send single packet (for testing)
     */
    fun sendTestPacket() {
        coroutineScope.launch {
            udpClient?.sendControllerState(ByteArray(22) { 0 })
        }
    }
}
