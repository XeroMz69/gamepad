package com.gamepad.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamepad.android.data.ConnectionSettings
import com.gamepad.android.data.ConnectionStatus
import com.gamepad.android.viewmodel.GamepadViewModel

@Composable
fun ConnectionScreen(viewModel: GamepadViewModel) {
    val connectionSettings by viewModel.connectionSettings.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val latency by viewModel.latency.collectAsState()
    val packetLoss by viewModel.packetLoss.collectAsState()
    
    var ipInput by remember { mutableStateOf(connectionSettings.pcIpAddress) }
    var portInput by remember { mutableStateOf(connectionSettings.port.toString()) }
    var isConnecting by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Connection Settings",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Status indicator
            StatusIndicator(connectionStatus)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Connection info display
            if (connectionStatus == ConnectionStatus.CONNECTED) {
                ConnectionInfoBox(latency, packetLoss)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Input section
            Text(
                text = "PC Server Settings",
                fontSize = 16.sp,
                color = Color(0xFF03DAC6),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            )
            
            // IP Address Input
            OutlinedTextField(
                value = ipInput,
                onValueChange = { ipInput = it },
                label = { Text("PC IP Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                enabled = connectionStatus == ConnectionStatus.DISCONNECTED,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF03DAC6),
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedLabelColor = Color(0xFF03DAC6),
                    disabledTextColor = Color(0xFF666666),
                    disabledBorderColor = Color(0xFF333333)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            // Port Input
            OutlinedTextField(
                value = portInput,
                onValueChange = { portInput = it },
                label = { Text("Port") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = connectionStatus == ConnectionStatus.DISCONNECTED,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF03DAC6),
                    unfocusedBorderColor = Color(0xFF333333),
                    focusedLabelColor = Color(0xFF03DAC6),
                    disabledTextColor = Color(0xFF666666),
                    disabledBorderColor = Color(0xFF333333)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            // Connect/Disconnect Button
            Button(
                onClick = {
                    if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                        // Validate input
                        val port = portInput.toIntOrNull()
                        if (ipInput.isNotEmpty() && port != null && port in 1..65535) {
                            isConnecting = true
                            viewModel.updateConnectionSettings(
                                ConnectionSettings(
                                    pcIpAddress = ipInput,
                                    port = port
                                )
                            )
                            viewModel.setConnectionStatus(ConnectionStatus.CONNECTING)
                            viewModel.addDebugLog("Attempting to connect to $ipInput:$port")
                        } else {
                            viewModel.addDebugLog("Invalid IP or port")
                        }
                    } else if (connectionStatus == ConnectionStatus.CONNECTED) {
                        viewModel.setConnectionStatus(ConnectionStatus.DISCONNECTED)
                        viewModel.addDebugLog("Disconnected from server")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (connectionStatus == ConnectionStatus.CONNECTED) 
                        Color(0xFFE53935) else Color(0xFF03DAC6)
                )
            ) {
                Text(
                    text = if (connectionStatus == ConnectionStatus.CONNECTED) 
                        "Disconnect" else "Connect",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
            
            // Test Connection Button
            Button(
                onClick = {
                    if (connectionStatus == ConnectionStatus.CONNECTED) {
                        viewModel.addDebugLog("Ping test: ${latency}ms latency, ${String.format("%.2f", packetLoss)}% loss")
                    } else {
                        viewModel.addDebugLog("Not connected to server")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF444444)
                )
            ) {
                Text("Ping PC Server", color = Color.White, fontSize = 16.sp)
            }
            
            // Help section
            HelpSection()
        }
    }
}

@Composable
private fun StatusIndicator(status: ConnectionStatus) {
    val statusText = when (status) {
        ConnectionStatus.DISCONNECTED -> "Disconnected"
        ConnectionStatus.CONNECTING -> "Connecting..."
        ConnectionStatus.CONNECTED -> "Connected"
        ConnectionStatus.RECONNECTING -> "Reconnecting..."
        ConnectionStatus.ERROR -> "Error"
    }
    
    val statusColor = when (status) {
        ConnectionStatus.DISCONNECTED -> Color(0xFFE53935)
        ConnectionStatus.CONNECTING -> Color(0xFFFFA726)
        ConnectionStatus.CONNECTED -> Color(0xFF66BB6A)
        ConnectionStatus.RECONNECTING -> Color(0xFFFFA726)
        ConnectionStatus.ERROR -> Color(0xFFE53935)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp)
                .background(statusColor, shape = androidx.compose.foundation.shape.CircleShape)
        )
        
        Text(
            text = statusText,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ConnectionInfoBox(latency: Int, packetLoss: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Latency:", fontSize = 14.sp, color = Color(0xFF999999))
            Text("${latency}ms", fontSize = 14.sp, color = Color.White)
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Packet Loss:", fontSize = 14.sp, color = Color(0xFF999999))
            Text(String.format("%.2f%%", packetLoss), fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
private fun HelpSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Connection Help",
            fontSize = 14.sp,
            color = Color(0xFF03DAC6),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Text(
            text = "1. Ensure PC server is running\n" +
                    "2. Enter PC's IP address (e.g., 192.168.1.100)\n" +
                    "3. Default port is 26760\n" +
                    "4. Both devices must be on the same Wi-Fi network\n" +
                    "5. Tap Connect to establish connection",
            fontSize = 12.sp,
            color = Color(0xFF999999),
            lineHeight = 18.sp
        )
    }
}
