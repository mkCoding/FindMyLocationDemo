package com.example.findmylocation.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import android.provider.Settings
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmylocation.locationaccess.UserLocationDetails

@Composable
fun FindMyLocationScreen(
    context: Context,
    locationUIState: LocationUiState,
   // onCheckPermission:() -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            shape = RoundedCornerShape(50.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp )
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Title
                Text(
                    text = "Find My Location",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                when(locationUIState){
                    is LocationUiState.Loading -> {
                        CircularProgressIndicator(strokeWidth = 6.dp)
                        Text(
                            text = "Finding location....",
                            fontSize = 32.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                    is LocationUiState.Success->{
                        LocationField(label = "Continent", value = "${locationUIState.data.continent}")
                        LocationField(label = "Country","${locationUIState.data.country}")
                        LocationField(label = "State", "${locationUIState.data.state}")
                        LocationField(label ="City", "${locationUIState.data.city}")
                        LocationField(label ="Street","${locationUIState.data.street}")
                        LocationField(label ="Street Address","${locationUIState.data.fullStreet}")
                        LocationField(label = "Zip", "${locationUIState.data.zip}")
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
                        Button(onClick = onOpenSettings) {
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
                            onClick = onRequestPermission , // will trigger permission check again
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                        ) {
                            Text("Allow Location Access", fontSize = 18.sp)
                        }
                        Text(
                            text = "Tap button → Allow in popup",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    is LocationUiState.Error -> {
                        Button(onClick = onRetry) {
                            Text("Try Again")
                        }
                    }

                    else -> {}

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

    Text(value, fontSize = 24.sp, fontWeight = FontWeight.Medium)

    Spacer(modifier = Modifier.height( 16.dp))
}


@Preview(showBackground = true)
@Composable
fun FindMyLocationScreenPreview() {
    val context = LocalContext.current
    FindMyLocationScreen(
        context = context,
        locationUIState = LocationUiState.Success(
            data = UserLocationDetails(
                continent = "North America",
                country = "United States",
                state = "GA",
                city = "Atlanta",
                street = "Monroe Dr",
                fullStreet = "1445 Monroe Dr",
                zip = "30324"
            )
        ),
        //onCheckPermission = {},
        onRequestPermission = {},
        onOpenSettings = {},
        onRetry = {}


    )
}