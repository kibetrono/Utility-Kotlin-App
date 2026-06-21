package com.example.utilityapp.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.utilityapp.viewmodels.CountryViewModel
import com.example.utilityapp.viewmodels.PopulationFormat
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UtilityScreen(modifier: Modifier = Modifier) {
    val viewModel: CountryViewModel = koinViewModel()
    val imageLoader: ImageLoader = koinInject()

    val country by viewModel.displayedCountry.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val notFound by viewModel.notFound.collectAsState()
    val populationFormat by viewModel.populationFormat.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val populationRank by viewModel.populationRank.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Country Info", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Search bar ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            label = { Text("Search a country") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { viewModel.confirmSearch() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { viewModel.showRandomCountry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Show me a random country", style = MaterialTheme.typography.bodyLarge)
        }

        // Everything below fills the remaining space and is centered as one block,
        // so there's no dead gap between the button and the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(48.dp))

                error != null -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadCountries() }) { Text("Retry") }
                }

                notFound -> Text(
                    "No country found matching \"$searchQuery\"",
                    style = MaterialTheme.typography.titleMedium
                )

                country != null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CountryDisplay(
                        name = country!!.name,
                        population = country!!.population,
                        flagUrl = country!!.flagUrl,
                        format = populationFormat,
                        rank = populationRank,
                        totalCountries = viewModel.totalCountryCount,
                        imageLoader = imageLoader
                    )

                    // Recent searches — only shown once the user has confirmed at least one search
                    if (recentSearches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Recent Searches",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recentSearches.forEach { recentName ->
                                AssistChip(
                                    onClick = { viewModel.selectRecentSearch(recentName) },
                                    label = { Text(recentName) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Card showing one country's flag, name, population and population rank
@Composable
fun CountryDisplay(
    name: String,
    population: Long,
    flagUrl: String,
    format: PopulationFormat,
    rank: Int?,
    totalCountries: Int,
    imageLoader: ImageLoader
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp, 110.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = flagUrl,
                    contentDescription = "$name flag",
                    imageLoader = imageLoader,
                    modifier = Modifier.size(128.dp, 88.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Population: ${formatPopulation(population, format)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            if (rank != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Population rank: #$rank of $totalCountries",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

// Formats population according to the user's chosen display setting
fun formatPopulation(population: Long, format: PopulationFormat): String {
    return when (format) {
        PopulationFormat.FULL -> "%,d".format(population)
        PopulationFormat.ABBREVIATED -> when {
            population >= 1_000_000_000 -> "%.1fB".format(population / 1_000_000_000.0)
            population >= 1_000_000 -> "%.1fM".format(population / 1_000_000.0)
            population >= 1_000 -> "%.1fK".format(population / 1_000.0)
            else -> population.toString()
        }
    }
}