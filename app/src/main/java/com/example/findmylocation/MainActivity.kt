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

class MainActivity : ComponentActivity() {
  private val viewModel: FindMyLocationViewModel by viewModels()

//    val locationPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // Permission was granted → get location again
//            viewModel.loadLocation(this@MainActivity)
//        }
//        // if denied → UI stays on PermissionRequired, user can tap again
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val location = rememberLocationHandler()
            val state by location.uiState.collectAsState()

            FindMyLocationTheme {
                val context = LocalContext.current

                // 2. Create ViewModel inside setContent (exactly like you already have)
//                val viewModel: FindMyLocationViewModel = viewModel()

//                LaunchedEffect(Unit) {
//                    viewModel.loadLocation(context)
//                }
//                val locationUIState by viewModel.uiState.collectAsState()

//                val onCheckPermission = {
//                    viewModel.loadLocation(context)
//                }

                FindMyLocationScreen(
                    context = context,
                    locationUIState = state,
                   // onCheckPermission = onCheckPermission,
                    onRequestPermission = location.requestPermission,
                    onOpenSettings =location.openSettings,
                    onRetry = location.retry

                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FindMyLocationTheme {
        Greeting("Android")
    }
}