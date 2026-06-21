package com.example.utilityapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utilityapp.data.Country
import com.example.utilityapp.data.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PopulationFormat { FULL, ABBREVIATED }
enum class AppTheme { LIGHT, DARK, SYSTEM }

class CountryViewModel(
    private val countryRepository: CountryRepository
) : ViewModel() {

    // Full list fetched once from the API, used for search lookups and random picks
    private var allCountries: List<Country> = emptyList()
    val totalCountryCount: Int get() = allCountries.size

    // The single country currently displayed on the main screen
    private val _displayedCountry = MutableStateFlow<Country?>(null)
    val displayedCountry: StateFlow<Country?> = _displayedCountry

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _notFound = MutableStateFlow(false)
    val notFound: StateFlow<Boolean> = _notFound

    // History of recently searched country names, most recent first, capped at 5
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    // This country's population rank out of all countries (1 = highest population)
    private val _populationRank = MutableStateFlow<Int?>(null)
    val populationRank: StateFlow<Int?> = _populationRank

    // --- Settings state ---
    private val _populationFormat = MutableStateFlow(PopulationFormat.FULL)
    val populationFormat: StateFlow<PopulationFormat> = _populationFormat

    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM)
    val appTheme: StateFlow<AppTheme> = _appTheme

    init {
        loadCountries()
    }

    // Fetch all countries once, then show a random one as the default
    fun loadCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                allCountries = countryRepository.getCountries()
                _displayedCountry.value = allCountries.randomOrNull()
                _displayedCountry.value?.let { updatePopulationRank(it) }
            } catch (e: Exception) {
                _error.value = "Could not load countries. Check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Called as the user types — updates the live preview but does NOT log to history
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _notFound.value = false

        if (query.isBlank()) return

        val match = allCountries.firstOrNull {
            it.name.equals(query, ignoreCase = true)
        } ?: allCountries.firstOrNull {
            it.name.startsWith(query, ignoreCase = true)
        }

        if (match != null) {
            _displayedCountry.value = match
        } else {
            _notFound.value = true
        }
    }

    // Called when the user confirms a search (presses search/enter on the keyboard)
    fun confirmSearch() {
        val current = _displayedCountry.value ?: return
        if (_searchQuery.value.isBlank()) return
        addToRecentSearches(current.name)
        updatePopulationRank(current)
    }

    // Show a fresh random country, clearing any active search
    fun showRandomCountry() {
        _searchQuery.value = ""
        _notFound.value = false
        _displayedCountry.value = allCountries.randomOrNull()
        _displayedCountry.value?.let { updatePopulationRank(it) }
    }

    // Jump back to a previously searched country by name
    fun selectRecentSearch(name: String) {
        _searchQuery.value = name
        val match = allCountries.firstOrNull { it.name == name }
        if (match != null) {
            _displayedCountry.value = match
            updatePopulationRank(match)
        }
    }

    fun setPopulationFormat(format: PopulationFormat) {
        _populationFormat.value = format
    }

    fun setAppTheme(theme: AppTheme) {
        _appTheme.value = theme
    }

    // Add a country name to recent searches, deduplicated, most recent first, capped at 5
    private fun addToRecentSearches(name: String) {
        val updated = listOf(name) + _recentSearches.value.filter { it != name }
        _recentSearches.value = updated.take(5)
    }

    // Recalculate where this country ranks by population among all countries
    private fun updatePopulationRank(country: Country) {
        val sorted = allCountries.sortedByDescending { it.population }
        val rank = sorted.indexOfFirst { it.name == country.name }
        _populationRank.value = if (rank >= 0) rank + 1 else null
    }
}