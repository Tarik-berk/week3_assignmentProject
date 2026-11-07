package com.example.week3_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.week3_assignment.ui.theme.Week3_assignmentTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SmartDeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Week3_assignmentTheme {
                SmartDeviceScreen(viewModel = viewModel)
            }
        }
    }
}