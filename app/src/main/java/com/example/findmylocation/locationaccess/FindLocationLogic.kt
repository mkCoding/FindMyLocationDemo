package com.example.findmylocation.locationaccess

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

class FindLocationLogic(
    private val context:Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun findMyLocation(
        onResult: (UserLocationDetails?) -> Unit,
        onPermissionDenied: () -> Unit,
        onLocationDisabled: () -> Unit
    ) {
        // 1. Check permissions
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionDenied()
            return
        }

        // 2. Check if location is enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled) {
            onLocationDisabled()
            return
        }

        // 3. Use proper request with CancellationToken
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location == null) {
                onResult(null)
                return@addOnSuccessListener
            }

            reverseGeocode(location) { details ->
                onResult(details)
            }
        }.addOnFailureListener { e ->
            Log.e("Location", "Failed to get location", e)
            onResult(null)
        }
    }

    private var cancellationTokenSource = CancellationTokenSource()

    private fun reverseGeocode(location: Location, callback: (UserLocationDetails?) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) { addresses ->
                processAddresses(addresses, callback)
            }
        } else {
            @Suppress("DEPRECATION")
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                processAddresses(addresses, callback)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    private fun processAddresses(
        addresses: List<Address>?,
        callback: (UserLocationDetails?) -> Unit
    ) {
        if (addresses.isNullOrEmpty()) {
            callback(null)
            return
        }

        val address = addresses[0]
        val streetNumberNameOnly = listOfNotNull(
            address.subThoroughfare,
            address.thoroughfare
        ).joinToString(" ")

        val details = UserLocationDetails(
            continent = getContinentFromCountry(address.countryCode),
            country = address.countryName ?: "",
            state = address.adminArea ?: "",
            city = address.locality ?: "",
            street = address.thoroughfare ?: "",
            fullStreet = streetNumberNameOnly,
            zip = address.postalCode ?: ""
        )
        callback(details)
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