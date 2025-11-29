package com.example.findmylocation.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.findmylocation.locationaccess.FindLocationLogic
import com.example.findmylocation.locationaccess.UserLocationDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FindMyLocationViewModel (): ViewModel(){

    val findLocationLogic = FindLocationLogic()

    private val _locationDetails = MutableStateFlow<UserLocationDetails?>(null)
    val locationDetails = _locationDetails.asStateFlow()


    fun loadLocation(
        context: Context
    ){
        findLocationLogic.findMyLocation(context){details ->
            _locationDetails.value = details
        }
    }
}