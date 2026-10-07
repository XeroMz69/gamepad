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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.gamepad.android.data.ButtonLayout
import com.gamepad.android.data.ControllerLayout
import com.gamepad.android.viewmodel.GamepadViewModel

@Composable
fun LayoutEditorScreen(viewModel: GamepadViewModel) {
    val isEditMode by viewModel.isEditMode.collectAsState()
    val currentLayout by viewModel.currentLayout.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    
    var selectedButton by remember { mutableStateOf<String?>(null) }
    var selectedButtonLayout by remember { mutableStateOf<ButtonLayout?>(null) }
    
    if (isEditMode && selectedButton != null && selectedButtonLayout != null) {
        // Edit mode for individual button
        EditButtonPanel(
            buttonName = selectedButton!!,
            buttonLayout = selectedButtonLayout!!,
            onUpdate = { newLayout ->
                // Update layout (implementation in viewmodel)
                selectedButtonLayout = newLayout
                viewModel.addDebugLog("Updated $selectedButton position and size")
            },
            onClose = {
                selectedButton = null
                selectedButtonLayout = null
            }
        )
    } else {
        // Main layout editor view
        MainLayoutEditorView(
            viewModel = viewModel,
            currentLayout = currentLayout,
            currentProfile = currentProfile,
            profiles = profiles,
            isEditMode = isEditMode,
            onSelectButton = { buttonName, layout ->
                selectedButton = buttonName
                selectedButtonLayout = layout
            }
        )
    }
}

@Composable
private fun MainLayoutEditorView(
    viewModel: GamepadViewModel,
    currentLayout: ControllerLayout,
    currentProfile: String,
    profiles: List<String>,
    isEditMode: Boolean,
    onSelectButton: (String, ButtonLayout) -> Unit
) {
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
            // Title
            Text(
                text = if (isEditMode) "Edit Layout - $currentProfile" else "Layout Manager",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Profile selector
            ProfileSelector(
                currentProfile = currentProfile,
                profiles = profiles,
                onProfileSelected = { profile ->
                    viewModel.setCurrentProfile(profile)
                    viewModel.addDebugLog("Switched to profile: $profile")
                },
                onNewProfile = { newProfileName ->
                    viewModel.addProfile(newProfileName)
                    viewModel.setCurrentProfile(newProfileName)
                    viewModel.addDebugLog("Created new profile: $newProfileName")
                },
                onDeleteProfile = { profileName ->
                    if (profiles.size > 1) {
                        viewModel.deleteProfile(profileName)
                        viewModel.addDebugLog("Deleted profile: $profileName")
                    } else {
                        viewModel.addDebugLog("Cannot delete last profile")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isEditMode) {
                // Edit mode controls
                Text(
                    text = "Edit Mode Active",
                    fontSize = 16.sp,
                    color = Color(0xFF03DAC6),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Show editable buttons
                LayoutButtonsList(
                    layout = currentLayout,
                    onSelectButton = onSelectButton
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Exit edit mode
                Button(
                    onClick = {
                        viewModel.toggleEditMode()
                        viewModel.addDebugLog("Exited edit mode")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
                ) {
                    Text("Exit Edit Mode", color = Color.White)
                }
            } else {
                // Normal mode controls
                Text(
                    text = "Current Profile: $currentProfile",
                    fontSize = 14.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Button(
                    onClick = {
                        viewModel.toggleEditMode()
                        viewModel.addDebugLog("Entered edit mode")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
                ) {
                    Text("Enter Edit Mode", color = Color.Black, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        viewModel.updateLayout(com.gamepad.android.data.getDefaultLayout())
                        viewModel.addDebugLog("Reset layout to default")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
                ) {
                    Text("Reset to Default", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileSelector(
    currentProfile: String,
    profiles: List<String>,
    onProfileSelected: (String) -> Unit,
    onNewProfile: (String) -> Unit,
    onDeleteProfile: (String) -> Unit
) {
    var showNewProfileDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    
    Column {
        Text(
            text = "Profiles",
            fontSize = 16.sp,
            color = Color(0xFF03DAC6),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Profile list
        profiles.forEach { profile ->
            Button(
                onClick = { onProfileSelected(profile) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (profile == currentProfile) Color(0xFF03DAC6) else Color(0xFF333333)
                )
            ) {
                Text(
                    profile,
                    color = if (profile == currentProfile) Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // New profile button
        Button(
            onClick = { showNewProfileDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
        ) {
            Text("+ New Profile", color = Color.White, fontSize = 14.sp)
        }
    }
    
    // New profile dialog (simplified inline)
    if (showNewProfileDialog) {
        // Simple implementation: just create with default name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F1F1F))
                .padding(16.dp)
        ) {
            Text("New Profile Name:", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            androidx.compose.material3.OutlinedTextField(
                value = newProfileName,
                onValueChange = { newProfileName = it },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (newProfileName.isNotEmpty()) {
                            onNewProfile(newProfileName)
                            showNewProfileDialog = false
                            newProfileName = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
                ) {
                    Text("Create", color = Color.Black)
                }
                
                Button(
                    onClick = {
                        showNewProfileDialog = false
                        newProfileName = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LayoutButtonsList(
    layout: ControllerLayout,
    onSelectButton: (String, ButtonLayout) -> Unit
) {
    val buttons = listOf(
        "A" to layout.buttonA,
        "B" to layout.buttonB,
        "X" to layout.buttonX,
        "Y" to layout.buttonY,
        "LB" to layout.buttonLB,
        "RB" to layout.buttonRB,
        "LT" to layout.triggerLT,
        "RT" to layout.triggerRT,
        "Menu" to layout.buttonMenu,
        "View" to layout.buttonView,
        "Xbox" to layout.buttonXbox,
        "D-Pad Up" to layout.dpadUp,
        "D-Pad Down" to layout.dpadDown,
        "D-Pad Left" to layout.dpadLeft,
        "D-Pad Right" to layout.dpadRight,
        "Left Stick" to layout.leftStick,
        "Right Stick" to layout.rightStick
    )
    
    Column {
        buttons.forEach { (name, buttonLayout) ->
            Button(
                onClick = { onSelectButton(name, buttonLayout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(bottom = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
            ) {
                Text(
                    "$name (X: ${String.format("%.2f", buttonLayout.x)}, Y: ${String.format("%.2f", buttonLayout.y)})",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun EditButtonPanel(
    buttonName: String,
    buttonLayout: ButtonLayout,
    onUpdate: (ButtonLayout) -> Unit,
    onClose: () -> Unit
) {
    var x by remember { mutableStateOf(buttonLayout.x) }
    var y by remember { mutableStateOf(buttonLayout.y) }
    var width by remember { mutableStateOf(buttonLayout.width / 100) }  // Normalize to 0-1
    var height by remember { mutableStateOf(buttonLayout.height / 100) }
    var opacity by remember { mutableStateOf(buttonLayout.opacity) }
    
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
                text = "Edit: $buttonName",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // X Position
            Text("X Position: ${String.format("%.2f", x)}", fontSize = 14.sp, color = Color(0xFF999999))
            Slider(
                value = x,
                onValueChange = { x = it },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            
            // Y Position
            Text("Y Position: ${String.format("%.2f", y)}", fontSize = 14.sp, color = Color(0xFF999999))
            Slider(
                value = y,
                onValueChange = { y = it },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            
            // Width
            Text("Width: ${String.format("%.2f", width)}", fontSize = 14.sp, color = Color(0xFF999999))
            Slider(
                value = width,
                onValueChange = { width = it },
                valueRange = 0.05f..0.3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            
            // Height
            Text("Height: ${String.format("%.2f", height)}", fontSize = 14.sp, color = Color(0xFF999999))
            Slider(
                value = height,
                onValueChange = { height = it },
                valueRange = 0.05f..0.3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            
            // Opacity
            Text("Opacity: ${String.format("%.0f%%", opacity * 100)}", fontSize = 14.sp, color = Color(0xFF999999))
            Slider(
                value = opacity,
                onValueChange = { opacity = it },
                valueRange = 0.3f..1f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF03DAC6),
                    activeTrackColor = Color(0xFF03DAC6),
                    inactiveTrackColor = Color(0xFF333333)
                )
            )
            
            // Save button
            Button(
                onClick = {
                    val updatedLayout = ButtonLayout(
                        x = x,
                        y = y,
                        width = width * 100,
                        height = height * 100,
                        opacity = opacity
                    )
                    onUpdate(updatedLayout)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6))
            ) {
                Text("Save Changes", color = Color.Black, fontSize = 16.sp)
            }
            
            // Close button
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))
            ) {
                Text("Close", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
