package com.example.utilityapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.utilityapp.viewmodels.AppTheme
import com.example.utilityapp.viewmodels.CountryViewModel
import com.example.utilityapp.viewmodels.PopulationFormat
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val viewModel: CountryViewModel = koinViewModel()

    val populationFormat by viewModel.populationFormat.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // --- Population display format ---
        Text("Population Display", style = MaterialTheme.typography.titleMedium)
        Text(
            "Choose how population numbers are shown on the main screen.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = populationFormat == PopulationFormat.FULL,
                onClick = { viewModel.setPopulationFormat(PopulationFormat.FULL) },
                label = { Text("Full (1,417,492,000)") }
            )
            FilterChip(
                selected = populationFormat == PopulationFormat.ABBREVIATED,
                onClick = { viewModel.setPopulationFormat(PopulationFormat.ABBREVIATED) },
                label = { Text("Abbreviated (1.4B)") }
            )
        }

        HorizontalDivider()

        // --- App theme ---
        Text("App Theme", style = MaterialTheme.typography.titleMedium)
        Text(
            "Choose how the app looks.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = appTheme == AppTheme.LIGHT,
                onClick = { viewModel.setAppTheme(AppTheme.LIGHT) },
                label = { Text("Light") }
            )
            FilterChip(
                selected = appTheme == AppTheme.DARK,
                onClick = { viewModel.setAppTheme(AppTheme.DARK) },
                label = { Text("Dark") }
            )
            FilterChip(
                selected = appTheme == AppTheme.SYSTEM,
                onClick = { viewModel.setAppTheme(AppTheme.SYSTEM) },
                label = { Text("System") }
            )
        }
    }
}