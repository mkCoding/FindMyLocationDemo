package com.example.findmylocation.locationaccess

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findmylocation.presentation.FindMyLocationViewModel
import com.example.findmylocation.presentation.LocationUiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberLocationHandler(
    viewModel: FindMyLocationViewModel = viewModel()
): LocationHandler{
    // initialize context and activity
    val context = LocalContext.current

    // initialize launcher to register ActivityResults launcher
    val activity = context as ComponentActivity

    // Create and remember a permission launcher that survives
    // config changes

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission() // We want to request a single permission
    ) { isGranted: Boolean ->
        // this callback runs when user answers the permission dialog
        if(isGranted){
            // Permission granted, and get location
            viewModel.loadLocation()
        }
        // if denied do nothing
    }
    // Runs only once when this composable first appears on screen
    LaunchedEffect(Unit) {
        viewModel.loadLocation()
    }

    return remember{
        LocationHandler(
            uiState = viewModel.uiState,
            requestPermission = {
                launcher.launch(input = Manifest.permission.ACCESS_FINE_LOCATION)
            },
            openSettings = {
                activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            },
            retry = {
                viewModel.loadLocation()
            }
        )
    }
}

data class LocationHandler(
    val uiState: StateFlow<LocationUiState>,
    val requestPermission: () -> Unit,
    val openSettings: () -> Unit,
    val retry: () -> Unit
)