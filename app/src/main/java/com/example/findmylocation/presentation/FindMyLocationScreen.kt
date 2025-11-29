package com.example.findmylocation.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmylocation.locationaccess.FindLocationLogic
import com.example.findmylocation.locationaccess.UserLocationDetails

@Composable
fun FindMyLocationScreen(
    context: Context,
    locationDetailsState: UserLocationDetails?
) {
    //val context = LocalContext.current
    val findLocationLogic = FindLocationLogic()
    var userLocation by remember { mutableStateOf<UserLocationDetails?>(null) }

    LaunchedEffect(Unit) {
        findLocationLogic.findMyLocation(context = context) { locationDetails ->
            userLocation = locationDetails
        }
    }
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

                if (locationDetailsState == null) {
                    Text(
                        text = "Finding location....",
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                } else {
                    LocationField(label = "Continent", value = "${locationDetailsState.continent}")
                    LocationField(label = "Country","${locationDetailsState.country}")
                    LocationField(label = "State", "${locationDetailsState.state}")
                    LocationField(label ="City", "${locationDetailsState.city}")
                    LocationField(label ="Street","${locationDetailsState.street}")
                    LocationField(label ="Street Address","${locationDetailsState.fullStreet}")
                    LocationField(label = "Zip", "${locationDetailsState.zip}")
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
        locationDetailsState = UserLocationDetails(
            continent = "North America",
            country = "United States",
            state = "GA",
            city = "Atlanta",
            street = "Monroe Dr",
            fullStreet = "1445 Monroe Dr",
            zip = "30324"
        )
    )
}