package com.example.utilityapp.data

import com.example.utilityapp.api.CountryApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

// Repository implementation — fetches both endpoints and merges them by country name
class CountryRepositoryImpl(
    private val countryApi: CountryApi,
    private val dispatcher: CoroutineDispatcher
) : CountryRepository {

    override suspend fun getCountries(): List<Country> {
        return withContext(dispatcher) {
            val flags = countryApi.getFlags().data
            val populations = countryApi.getPopulations().data

            // Build a lookup of country name -> latest population value
            val populationByName = populations.associate { entry ->
                val latest = entry.populationCounts.maxByOrNull { it.year }
                entry.country.lowercase() to (latest?.value ?: 0L)
            }

            // Merge: only keep countries that have a flag (population data is a superset
            // that also includes regional aggregates like "Arab World", which we skip
            // automatically since they won't appear in the flags list)
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