package com.example.findmylocation.locationaccess

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.findmylocation.repository.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class FindLocationLogic(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): LocationResult =
        suspendCancellableCoroutine { cont ->

            // 1️⃣ Permission check (MANDATORY)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cont.resume(LocationResult.PermissionDenied)
                return@suspendCancellableCoroutine
            }

            // 2️⃣ Location enabled check (MANDATORY)
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // THIS is the line you highlighted: always check both providers
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            ) {
                cont.resume(LocationResult.LocationDisabled) // Resume coroutine if location is off
                return@suspendCancellableCoroutine
            }

            // 3️⃣ Cached location first
            fusedLocationClient.lastLocation
                .addOnSuccessListener { cached ->
                    if (cached != null) {
                        reverseGeocode(cached, cont)
                    } else {
                        requestFreshLocation(cont)
                    }
                }
                .addOnFailureListener {
                    cont.resume(LocationResult.Error("Failed to access location"))
                }
        }

    private fun requestFreshLocation(
        cont: CancellableContinuation<LocationResult>
    ) {
        val token = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            token.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                reverseGeocode(location, cont)
            } else {
                cont.resume(LocationResult.Error("Location unavailable"))
            }
        }.addOnFailureListener {
            cont.resume(LocationResult.Error("Location request failed"))
        }
    }

    private fun reverseGeocode(
        location: Location,
        cont: CancellableContinuation<LocationResult>
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                ) { addresses ->
                    val details = addresses
                        ?.firstOrNull()
                        ?.toUserLocationDetails()

                    cont.resume(
                        details?.let { LocationResult.Success(it) }
                            ?: LocationResult.Error("Address not found")
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)

                val details = addresses
                    ?.firstOrNull()
                    ?.toUserLocationDetails()

                cont.resume(
                    details?.let { LocationResult.Success(it) }
                        ?: LocationResult.Error("Address not found")
                )
            }
        } catch (e: Exception) {
            cont.resume(LocationResult.Error("Geocoding failed"))
        }
    }

    private fun Address.toUserLocationDetails(): UserLocationDetails {
        val street = listOfNotNull(
            subThoroughfare,
            thoroughfare
        ).joinToString(" ")

        return UserLocationDetails(
            continent = getContinentFromCountry(countryCode),
            country = countryName.orEmpty(),
            state = adminArea.orEmpty(),
            city = locality.orEmpty(),
            street = thoroughfare.orEmpty(),
            fullStreet = street,
            zip = postalCode.orEmpty()
        )
    }

    private fun getContinentFromCountry(code: String?): String =
        when (code) {
            "US", "CA", "MX" -> "North America"
            "BR", "AR", "CL" -> "South America"
            "FR", "DE", "IT", "GB", "ES", "NL" -> "Europe"
            "CN", "JP", "IN", "KR", "SG" -> "Asia"
            "AU", "NZ" -> "Oceania"
            "ZA", "EG", "NG", "KE" -> "Africa"
            else -> "Unknown"
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