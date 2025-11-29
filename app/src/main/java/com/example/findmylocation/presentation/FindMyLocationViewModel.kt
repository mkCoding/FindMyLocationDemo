package com.example.findmylocation.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.findmylocation.locationaccess.FindLocationLogic
import com.example.findmylocation.locationaccess.UserLocationDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FindMyLocationViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()


    fun loadLocation(
        context: Context
    ) {
        _uiState.value = LocationUiState.Loading

        FindLocationLogic(context).findMyLocation(
            onResult = { details ->
                _uiState.value = if (details != null) {
                    LocationUiState.Success(details)
                } else {
                    LocationUiState.Error("Unable to get address. Try again.")
                }

            },
            onPermissionDenied = {
                _uiState.value = LocationUiState.PermissionRequired
            },
            onLocationDisabled = {
                _uiState.value = LocationUiState.LocationDisabled
            }
        )
    }
}


sealed class LocationUiState {
    object Loading : LocationUiState()
    object PermissionRequired : LocationUiState()
    object LocationDisabled : LocationUiState()
    data class Success(val data: UserLocationDetails) : LocationUiState()
    data class Error(val message: String = "Something went wrong") : LocationUiState()
}