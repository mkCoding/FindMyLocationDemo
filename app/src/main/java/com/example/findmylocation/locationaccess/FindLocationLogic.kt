package com.example.findmylocation.locationaccess

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale
import java.util.jar.Manifest

class FindLocationLogic {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun findMyLocation(context: Context, onResult:(UserLocationDetails?)->Unit){
        //initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Check permissions before proceeding
        if(ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            onResult(null) // Permission missing, caller should handle
            return
        }

        // request the last known location
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
                if(location!=null){
                    // add geo decoder to turn lat and long into an address
                    val geocoder = Geocoder(context, Locale.getDefault())

                    try{
                        val addresses: MutableList<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                ?.toMutableList()
                                ?:mutableListOf()

                        if(addresses.isNotEmpty()){
                            val address = addresses?.get(0)
                            val streetNumberNameOnly = listOfNotNull(
                                address?.subThoroughfare,   // street number
                                address?.thoroughfare       // street name
                            ).joinToString(" ")


                            // populate data class
                            val userLocationDetails = UserLocationDetails(
                                continent = getContinentFromCountry(address?.countryCode),
                                country = address?.countryName?:"",
                                state = address?.adminArea?:"",
                                city = address?.locality?:"",
                                street = address?.thoroughfare?:"",
                                fullStreet = streetNumberNameOnly?:"",
                                zip = address?.postalCode?:""
                            )
                            onResult(userLocationDetails)
                        }else{
                            onResult(null)
                        }

                    }catch (e: IOException){
                        e.printStackTrace()
                        onResult(null)
                    }
                }

            }.addOnFailureListener { e->
                println("Error getting location ${e.message}")
            }
    }

    private fun getContinentFromCountry(countryCode: String?): String {
        return when (countryCode) {
            "US", "CA", "MX" -> "North America"
            "BR", "AR", "CL" -> "South America"
            "FR", "DE", "IT", "GB", "ES", "NL" -> "Europe"
            "CN", "JP", "IN", "KR", "SG" -> "Asia"
            "AU", "NZ" -> "Oceania"
            "ZA", "EG", "NG", "KE" -> "Africa"
            else -> "Unknown"
        }
    }

}

data class UserLocationDetails(
    val continent:String,
    val country:String,
    val state:String,
    val city:String,
    val street:String,
    val fullStreet:String,
    val zip:String
)