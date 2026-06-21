package com.example.utilityapp.data

import kotlinx.serialization.Serializable

// ----- Flags endpoint response shape -----
@Serializable
data class FlagResponse(
    val error: Boolean,
    val data: List<FlagEntry>
)

@Serializable
data class FlagEntry(
    val name: String,
    val flag: String,
    val iso2: String = "",
    val iso3: String = ""
)

// ----- Population endpoint response shape -----
@Serializable
data class PopulationResponse(
    val error: Boolean,
    val data: List<PopulationEntry>
)

@Serializable
data class PopulationEntry(
    val country: String,
    val iso3: String = "",
    val populationCounts: List<PopulationCount> = emptyList()
)

@Serializable
data class PopulationCount(
    val year: Int,
    val value: Long
)

// ----- Merged model used throughout the UI and ViewModel -----
data class Country(
    val name: String,
    val flagUrl: String,
    val population: Long
)