package com.example.findmylocation.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.example.findmylocation.locationaccess.FindLocationLogic
import com.example.findmylocation.locationaccess.UserLocationDetails
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val logic = FindLocationLogic(context)

    override suspend fun getCurrentLocation(): LocationResult =
        withTimeoutOrNull(10_000L) {
            logic.getCurrentLocation()
        } ?: LocationResult.Error("Location request timed out")
}

sealed class LocationResult {
    data class Success(val details: UserLocationDetails) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
}
