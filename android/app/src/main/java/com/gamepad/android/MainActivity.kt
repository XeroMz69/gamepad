package com.gamepad.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.gamepad.android.ui.theme.VirtualGamepadTheme
import com.gamepad.android.ui.screens.MainScreen
import com.gamepad.android.viewmodel.GamepadViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: GamepadViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GamepadViewModel::class.java)
        
        setContent {
            VirtualGamepadTheme {
                MainScreen(viewModel)
            }
        }
    }
}
