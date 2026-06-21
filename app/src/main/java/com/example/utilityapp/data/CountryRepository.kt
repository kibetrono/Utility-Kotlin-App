package com.example.utilityapp.data

// Repository interface — abstracts where the data comes from
interface CountryRepository {
    suspend fun getCountries(): List<Country>
}