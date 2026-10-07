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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamepad.android.data.ResponseCurve
import com.gamepad.android.viewmodel.GamepadViewModel

@Composable
fun SettingsScreen(viewModel: GamepadViewModel) {
    val settings by viewModel.controllerSettings.collectAsState()
    
    var deadzone by remember { mutableStateOf(settings.deadzone) }
    var sensitivity by remember { mutableStateOf(settings.sensitivity) }
    var responseCurve by remember { mutableStateOf(settings.responseCurve) }
    var invertX by remember { mutableStateOf(settings.invertX) }
    var invertY by remember { mutableStateOf(settings.invertY) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Controller Settings",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Deadzone Section
            SettingSection(title = "Deadzone")
            Text(
                text = "Deadzone: ${String.format("%.0f%%", deadzone * 100)}",
                fontSize = 14.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = deadzone,
                onValueChange = { 
                    deadzone = it
                    viewModel.setDeadzone(it)
                },
                valueRange = 0f..0.5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            Text(
                text = "Prevents small stick movements from registering (helps with drift)",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Sensitivity Section
            SettingSection(title = "Sensitivity")
            Text(
                text = "Sensitivity: ${String.format("%.1f", sensitivity)}x",
                fontSize = 14.sp,
                color = Color(0xFF999999),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = sensitivity,
                onValueChange = { 
                    sensitivity = it
                    viewModel.setSensitivity(it)
                },
                valueRange = 0.5f..2.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            Text(
                text = "1.0x = normal | 0.5x = slower | 2.0x = faster",
                fontSize = 12.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Response Curve Section
            SettingSection(title = "Response Curve")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResponseCurve.values().forEach { curve ->
                    Button(
                        onClick = {
                            responseCurve = curve
                            viewModel.setResponseCurve(curve)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (responseCurve == curve) Color(0xFF03DAC6) else Color(0xFF333333)
                        )
                    ) {
                        Text(
                            curve.name,
                            color = if (responseCurve == curve) Color.Black else Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Stick Inversion Section
            SettingSection(title = "Stick Inversion")
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Invert X Axis", fontSize = 14.sp, color = Color.White)
                Switch(
                    checked = invertX,
                    onCheckedChange = {
                        invertX = it
                        // Update in settings
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF03DAC6),
                        checkedTrackColor = Color(0xFF03DAC6),
                        uncheckedThumbColor = Color(0xFF666666),
                        uncheckedTrackColor = Color(0xFF333333)
                    )
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Invert Y Axis", fontSize = 14.sp, color = Color.White)
                Switch(
                    checked = invertY,
                    onCheckedChange = {
                        invertY = it
                        // Update in settings
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF03DAC6),
                        checkedTrackColor = Color(0xFF03DAC6),
                        uncheckedThumbColor = Color(0xFF666666),
                        uncheckedTrackColor = Color(0xFF333333)
                    )
                )
            }
            
            // Reset Button
            Button(
                onClick = {
                    viewModel.addDebugLog("Reset settings to default")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
            ) {
                Text("Reset to Default", color = Color.White, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingSection(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        color = Color(0xFF03DAC6),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
