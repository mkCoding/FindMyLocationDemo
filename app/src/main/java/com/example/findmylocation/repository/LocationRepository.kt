package com.example.findmylocation.repository


interface LocationRepository {

    suspend fun getCurrentLocation(): LocationResult
}