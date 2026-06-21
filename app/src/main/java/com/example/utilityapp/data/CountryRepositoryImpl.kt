package com.example.utilityapp.data

import com.example.utilityapp.api.CountryApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// Repository implementation — fetches both endpoints on a background thread and merges them
class CountryRepositoryImpl(
    private val countryApi: CountryApi,
    private val dispatcher: CoroutineDispatcher
) : CountryRepository {

    override suspend fun getCountries(): List<Country> {
        return withContext(dispatcher) {
            val flags = countryApi.getFlags().data
            val populations = countryApi.getPopulations().data

            // Lookup of country name -> latest known population value
            val populationByName = populations.associate { entry ->
                val latest = entry.populationCounts.maxByOrNull { it.year }
                entry.country.lowercase() to (latest?.value ?: 0L)
            }

            // Use the flags list as the base (it's the cleaner "real countries" list),
            // attaching population where a name match is found
            flags.map { flagEntry ->
                Country(
                    name = flagEntry.name,
                    flagUrl = flagEntry.flag,
                    population = populationByName[flagEntry.name.lowercase()] ?: 0L
                )
            }
        }
    }
}