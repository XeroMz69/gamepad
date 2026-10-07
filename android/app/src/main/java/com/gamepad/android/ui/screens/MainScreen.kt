package com.gamepad.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
import com.gamepad.android.viewmodel.GamepadViewModel

enum class MainTab {
    CONTROLLER,
    LAYOUT_EDITOR,
    TEST,
    SETTINGS,
    CONNECTION
}

@Composable
fun MainScreen(viewModel: GamepadViewModel) {
    var currentTab by remember { mutableStateOf(MainTab.CONTROLLER) }
    val isEditMode by viewModel.isEditMode.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content area (takes most of the space)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0A0A0A))
        ) {
            when (currentTab) {
                MainTab.CONTROLLER -> ControllerScreen(viewModel)
                MainTab.LAYOUT_EDITOR -> LayoutEditorScreen(viewModel)
                MainTab.TEST -> TestScreen(viewModel)
                MainTab.SETTINGS -> SettingsScreen(viewModel)
                MainTab.CONNECTION -> ConnectionScreen(viewModel)
            }
        }
        
        // Navigation bar at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF1F1F1F))
        ) {
            NavigationButton(
                label = "Controller",
                isSelected = currentTab == MainTab.CONTROLLER,
                onClick = { currentTab = MainTab.CONTROLLER }
            )
            NavigationButton(
                label = "Layout",
                isSelected = currentTab == MainTab.LAYOUT_EDITOR,
                onClick = { currentTab = MainTab.LAYOUT_EDITOR }
            )
            NavigationButton(
                label = "Test",
                isSelected = currentTab == MainTab.TEST,
                onClick = { currentTab = MainTab.TEST }
            )
            NavigationButton(
                label = "Settings",
                isSelected = currentTab == MainTab.SETTINGS,
                onClick = { currentTab = MainTab.SETTINGS }
            )
            NavigationButton(
                label = "Connection",
                isSelected = currentTab == MainTab.CONNECTION,
                onClick = { currentTab = MainTab.CONNECTION }
            )
        }
    }
}

@Composable
private fun NavigationButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(
                color = if (isSelected) Color(0xFF03DAC6) else Color(0xFF1F1F1F)
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Text(label)
        }
    }
}

// Placeholder screens for other tabs (will be implemented in later tasks)
@Composable
private fun LayoutEditorScreen(viewModel: GamepadViewModel) {
    com.gamepad.android.ui.screens.LayoutEditorScreen(viewModel)
}

@Composable
private fun TestScreen(viewModel: GamepadViewModel) {
    com.gamepad.android.ui.screens.TestScreen(viewModel)
}

@Composable
private fun SettingsScreen(viewModel: GamepadViewModel) {
    com.gamepad.android.ui.screens.SettingsScreen(viewModel)
}

@Composable
private fun ConnectionScreen(viewModel: GamepadViewModel) {
    com.gamepad.android.ui.screens.ConnectionScreen(viewModel)
}
