package com.gamepad.android.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

/**
 * UDP client for sending gamepad input to PC server
 * Sends packets @ 60Hz (every ~16.67ms)
 */
class GamepadUdpClient(
    private val serverIp: String,
    private val serverPort: Int,
    private val onConnected: () -> Unit = {},
    private val onDisconnected: () -> Unit = {},
    private val onLatencyUpdate: (Int) -> Unit = {},
    private val onError: (String) -> Unit = {}
) {
    
    private var socket: DatagramSocket? = null
    private var isConnected = false
    private var sequenceNumber: UInt = 0u
    
    companion object {
        const val TAG = "GamepadUdpClient"
        const val SEND_INTERVAL_MS = 16L  // ~60Hz
        const val SOCKET_TIMEOUT_MS = 5000
        const val HEARTBEAT_INTERVAL_MS = 100L
    }
    
    /**
     * Connect to server and start sending thread
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = DatagramSocket().apply {
                soTimeout = SOCKET_TIMEOUT_MS
            }
            
            val serverAddress = InetAddress.getByName(serverIp)
            Log.d(TAG, "Connecting to $serverIp:$serverPort")
            
            // Send initial handshake packet
            val handshakePacket = createHandshakePacket()
            val packet = DatagramPacket(
                handshakePacket,
                handshakePacket.size,
                serverAddress,
                serverPort
            )
            socket!!.send(packet)
            
            isConnected = true
            onConnected()
            Log.d(TAG, "Connected successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed: ${e.message}")
            onError("Failed to connect: ${e.message}")
            isConnected = false
            false
        }
    }
    
    /**
     * Disconnect from server
     */
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            socket?.close()
            isConnected = false
            onDisconnected()
            Log.d(TAG, "Disconnected")
        } catch (e: Exception) {
            Log.e(TAG, "Disconnect error: ${e.message}")
        }
    }
    
    /**
     * Send controller state packet
     * Returns true if successful
     */
    suspend fun sendControllerState(stateBytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected || socket == null) {
            return@withContext false
        }
        
        try {
            // Fill in sequence number and timestamp
            val packet = fillPacketHeader(stateBytes)
            
            val datagramPacket = DatagramPacket(
                packet,
                packet.size,
                InetAddress.getByName(serverIp),
                serverPort
            )
            
            val sendTime = System.currentTimeMillis()
            socket!!.send(datagramPacket)
            
            // Try to receive ACK for latency calculation
            tryReceiveAck(sendTime)
            
            sequenceNumber++
            true
        } catch (e: SocketTimeoutException) {
            // Timeout is normal, continue sending
            false
        } catch (e: Exception) {
            Log.e(TAG, "Send error: ${e.message}")
            isConnected = false
            onDisconnected()
            false
        }
    }
    
    /**
     * Try to receive ACK packet to calculate latency
     */
    private fun tryReceiveAck(sendTime: Long) {
        try {
            val buffer = ByteArray(8)  // ACK packet is minimal
            val packet = DatagramPacket(buffer, buffer.size)
            
            // Set short timeout for ACK reception
            val originalTimeout = socket?.soTimeout ?: SOCKET_TIMEOUT_MS
            socket?.soTimeout = 10  // 10ms timeout for ACK
            
            try {
                socket?.receive(packet)
                val latency = (System.currentTimeMillis() - sendTime).toInt()
                onLatencyUpdate(latency)
            } finally {
                socket?.soTimeout = originalTimeout
            }
        } catch (e: SocketTimeoutException) {
            // ACK timeout is expected, it's optional
        } catch (e: Exception) {
            // Ignore other errors
        }
    }
    
    /**
     * Send continuous stream of controller states
     * This should be called from a coroutine scope
     */
    suspend fun sendContinuousStream(
        stateProvider: suspend () -> ByteArray?
    ) = withContext(Dispatchers.IO) {
        var lastSendTime = System.currentTimeMillis()
        
        while (isActive && isConnected) {
            try {
                val currentTime = System.currentTimeMillis()
                val timeSinceLastSend = currentTime - lastSendTime
                
                if (timeSinceLastSend >= SEND_INTERVAL_MS) {
                    val state = stateProvider()
                    if (state != null) {
                        sendControllerState(state)
                        lastSendTime = currentTime
                    }
                }
                
                // Sleep to prevent busy waiting
                Thread.sleep(1)
            } catch (e: Exception) {
                Log.e(TAG, "Stream error: ${e.message}")
                isConnected = false
                onDisconnected()
                break
            }
        }
    }
    
    /**
     * Fill packet header with sequence number and timestamp
     */
    private fun fillPacketHeader(packet: ByteArray): ByteArray {
        if (packet.size < 12) {
            return packet
        }
        
        // Sequence number (4 bytes, starting at offset 0)
        packet[0] = (sequenceNumber shr 24).toByte()
        packet[1] = (sequenceNumber shr 16).toByte()
        packet[2] = (sequenceNumber shr 8).toByte()
        packet[3] = sequenceNumber.toByte()
        
        // Timestamp (8 bytes, starting at offset 4)
        val timestamp = System.currentTimeMillis()
        packet[4] = (timestamp shr 56).toByte()
        packet[5] = (timestamp shr 48).toByte()
        packet[6] = (timestamp shr 40).toByte()
        packet[7] = (timestamp shr 32).toByte()
        packet[8] = (timestamp shr 24).toByte()
        packet[9] = (timestamp shr 16).toByte()
        packet[10] = (timestamp shr 8).toByte()
        packet[11] = timestamp.toByte()
        
        return packet
    }
    
    /**
     * Create handshake packet for initial connection
     */
    private fun createHandshakePacket(): ByteArray {
        // Simple handshake: all zeros (will be filled with sequence/timestamp)
        return ByteArray(22)
    }
    
    fun getSequenceNumber(): UInt = sequenceNumber
    
    fun isConnected(): Boolean = isConnected && socket != null
}
