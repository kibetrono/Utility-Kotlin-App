package com.example.utilityapp.api

import com.example.utilityapp.data.FlagResponse
import com.example.utilityapp.data.PopulationResponse
import retrofit2.http.GET

// Retrofit interface — defines the two endpoints we call, both simple GETs, no auth needed
interface CountryApi {

    @GET("api/v0.1/countries/flag/images")
    suspend fun getFlags(): FlagResponse

    @GET("api/v0.1/countries/population")
    suspend fun getPopulations(): PopulationResponse
}