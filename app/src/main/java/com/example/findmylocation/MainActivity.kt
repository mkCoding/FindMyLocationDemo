package com.example.findmylocation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findmylocation.locationaccess.rememberLocationHandler
import com.example.findmylocation.presentation.FindMyLocationScreen
import com.example.findmylocation.presentation.FindMyLocationViewModel
import com.example.findmylocation.ui.theme.FindMyLocationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: FindMyLocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val location = rememberLocationHandler()
            val state by viewModel.uiState.collectAsState()  // Collect directly from ViewModel
            val loadLocationAction = { viewModel.loadLocation() }

            FindMyLocationTheme {
                FindMyLocationScreen(
                    locationUiState = state,
                    loadLocation = loadLocationAction,
                    onRequestPermission = location.requestPermission
                )
            }
        }

        // Trigger initial load
        viewModel.loadLocation()
    }
}
