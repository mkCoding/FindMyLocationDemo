package com.example.findmylocation.presentation

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmylocation.locationaccess.FindLocationLogic
import com.example.findmylocation.locationaccess.UserLocationDetails
import com.example.findmylocation.repository.LocationRepository
import com.example.findmylocation.repository.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindMyLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()


    fun loadLocation() {
        viewModelScope.launch {
            _uiState.value = LocationUiState.Loading

            // 1️⃣ Check for network availability first
//            if (!context.isNetworkAvailable()) {
//                _uiState.value =
//                    LocationUiState.Error("No internet connection. Please check Wi-Fi or mobile data.")
//                return@launch // Stop further execution
//            }

            when (val result = locationRepository.getCurrentLocation()) {

                is LocationResult.Success -> {
                    _uiState.value =
                        LocationUiState.Success(result.details)
                }

                is LocationResult.PermissionDenied -> {
                    _uiState.value =
                        LocationUiState.PermissionRequired
                }

                is LocationResult.LocationDisabled -> {
                    _uiState.value =
                        LocationUiState.LocationDisabled
                }

                is LocationResult.Error -> {
                    _uiState.value =
                        LocationUiState.Error(result.message)

                }
            }
        }
    }


}



fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

sealed class LocationUiState {
    object Loading : LocationUiState()
    object PermissionRequired : LocationUiState()
    object LocationDisabled : LocationUiState()
    data class Success(val data: UserLocationDetails) : LocationUiState()
    data class Error(val message: String = "Something went wrong") : LocationUiState()
}


