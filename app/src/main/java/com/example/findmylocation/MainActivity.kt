package com.example.findmylocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findmylocation.presentation.FindMyLocationScreen
import com.example.findmylocation.presentation.FindMyLocationViewModel
import com.example.findmylocation.ui.theme.FindMyLocationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindMyLocationTheme {
                val context = LocalContext.current
                val viewModel: FindMyLocationViewModel = viewModel()
                LaunchedEffect(Unit) {
                    viewModel.loadLocation(context)
                }
                val locationDetailsState by viewModel.locationDetails.collectAsState()
                FindMyLocationScreen(
                    context = context,
                    locationDetailsState = locationDetailsState
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