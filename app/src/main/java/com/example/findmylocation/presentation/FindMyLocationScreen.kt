package com.example.findmylocation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmylocation.locationaccess.UserLocationDetails

@Composable
fun FindMyLocationScreen(
    locationUiState: LocationUiState,
    loadLocation:() -> Unit,
    onRequestPermission: () -> Unit, // <-- pass this from Composable
) {
  //  val locationUIState by viewModel.uiState.collectAsState()

    // Trigger initial load
    LaunchedEffect(Unit) {
        loadLocation()
    }

    val loadLocationAction = { loadLocation() }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Find My Location",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        when (locationUiState) {
            is LocationUiState.Loading -> {
                CircularProgressIndicator(strokeWidth = 6.dp)
                Text(
                    text = "Finding location....",
                    fontSize = 32.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
            is LocationUiState.Success -> {
                val details = locationUiState.data
                LocationField("Continent", details.continent)
                LocationField("Country", details.country)
                LocationField("State", details.state)
                LocationField("City", details.city)
                LocationField("Street", details.street)
                LocationField("Street Address", details.fullStreet)
                LocationField("Zip", details.zip)


                Spacer(Modifier.height(24.dp))
                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = loadLocationAction) {
                    Text(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        text = "Refresh Location")
                }
            }
            is LocationUiState.LocationDisabled -> {
                Icon(
                    Icons.Default.LocationDisabled,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Please turn on GPS / Location",
                    fontSize = 20.sp,
                    color = Color(0xFFFF9800),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = {loadLocation() }) {
                    Text("Open Location Settings")
                }
            }
            is LocationUiState.PermissionRequired -> {
                Icon(
                    Icons.Default.LocationOff,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Location permission is required",
                    fontSize = 20.sp,
                    color = Color(0xFFE91E63),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onRequestPermission ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("Allow Location Access", fontSize = 18.sp)
                }
            }
            is LocationUiState.Error -> {
                val message = (locationUiState as LocationUiState.Error).message
                Text("Error: $message", color = Color.Red)
                Spacer(Modifier.height(16.dp))
                Button(onClick = loadLocation) {
                    Text("Try Again")
                }
            }
        }
    }
}


@Composable
fun LocationField(
    label:String,
    value:String
){
    Text(
        "$label",
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )

    Text(value, fontSize = 24.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)

    Spacer(modifier = Modifier.height( 16.dp))
}


@Preview(showBackground = true)
@Composable
fun FindMyLocationScreenPreview() {
    FindMyLocationScreen(
        locationUiState = LocationUiState.Success(
            data = UserLocationDetails(
                continent = "North America",
                country = "United States",
                state = "GA",
                city = "Atlanta",
                street = "Monroe Dr",
                fullStreet = "7045 180th Avenue North East",
                zip = "30324"
            )
        ),
        loadLocation = {},
        onRequestPermission = {}

    )
}