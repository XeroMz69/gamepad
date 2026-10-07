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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamepad.android.viewmodel.GamepadViewModel

@Composable
fun TestScreen(viewModel: GamepadViewModel) {
    val controllerState by viewModel.controllerState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val latency by viewModel.latency.collectAsState()
    val debugLogs by viewModel.debugLogs.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Controller Tester",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Connection Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        "Status: ${connectionStatus.name}",
                        fontSize = 14.sp,
                        color = when (connectionStatus) {
                            com.gamepad.android.data.ConnectionStatus.CONNECTED -> Color(0xFF66BB6A)
                            com.gamepad.android.data.ConnectionStatus.DISCONNECTED -> Color(0xFFE53935)
                            else -> Color(0xFFFFA726)
                        }
                    )
                    Text(
                        "Latency: ${latency}ms",
                        fontSize = 12.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Button States
            Text(
                text = "Buttons",
                fontSize = 16.sp,
                color = Color(0xFF03DAC6),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonIndicator("A", controllerState.buttonA)
                ButtonIndicator("B", controllerState.buttonB)
                ButtonIndicator("X", controllerState.buttonX)
                ButtonIndicator("Y", controllerState.buttonY)
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonIndicator("LB", controllerState.buttonLB)
                ButtonIndicator("RB", controllerState.buttonRB)
                ButtonIndicator("Menu", controllerState.buttonMenu)
                ButtonIndicator("View", controllerState.buttonView)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Analog Sticks
            Text(
                text = "Analog Sticks",
                fontSize = 16.sp,
                color = Color(0xFF03DAC6),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            StickDisplay("Left Stick", controllerState.leftStickX, controllerState.leftStickY)
            StickDisplay("Right Stick", controllerState.rightStickX, controllerState.rightStickY)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Triggers
            Text(
                text = "Triggers",
                fontSize = 16.sp,
                color = Color(0xFF03DAC6),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            TriggerDisplay("LT", controllerState.triggerLT)
            TriggerDisplay("RT", controllerState.triggerRT)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Debug Logs
            Text(
                text = "Debug Logs",
                fontSize = 16.sp,
                color = Color(0xFF03DAC6),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                items(debugLogs) { log ->
                    Text(
                        log,
                        fontSize = 11.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Clear Logs Button
            Button(
                onClick = { viewModel.clearDebugLogs() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
            ) {
                Text("Clear Logs", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ButtonIndicator(label: String, pressed: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(
                color = if (pressed) Color(0xFF03DAC6) else Color(0xFF333333),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = if (pressed) Color.Black else Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun StickDisplay(label: String, x: Float, y: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.White)
        Text(
            "X: ${String.format("%.2f", x)}, Y: ${String.format("%.2f", y)}",
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
    }
}

@Composable
private fun TriggerDisplay(label: String, value: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.White)
        Text(
            "${String.format("%.0f%%", value * 100)}",
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
    }
}
