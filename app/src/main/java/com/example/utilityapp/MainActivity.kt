package com.example.utilityapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.utilityapp.di.appModules
import com.example.utilityapp.screens.SettingsScreen
import com.example.utilityapp.screens.UtilityScreen
import com.example.utilityapp.ui.theme.UtilityAppTheme
import com.example.utilityapp.viewmodels.AppTheme
import com.example.utilityapp.viewmodels.CountryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

// Initialises Koin when the app starts
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModules)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UtilityApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UtilityAppPreview() {
    UtilityApp()
}

@Composable
fun UtilityApp() {
    var selectedTab by remember { mutableStateOf("Utility") }

    val viewModel: CountryViewModel = koinViewModel()
    val appTheme by viewModel.appTheme.collectAsState()
    val isDarkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    UtilityAppTheme(darkTheme = isDarkTheme) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.utility_label)) },
                        label = { Text(stringResource(R.string.utility_label)) },
                        selected = selectedTab == "Utility",
                        onClick = { selectedTab = "Utility" }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_label)) },
                        label = { Text(stringResource(R.string.settings_label)) },
                        selected = selectedTab == "Settings",
                        onClick = { selectedTab = "Settings" }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    "Utility" -> UtilityScreen()
                    "Settings" -> SettingsScreen()
                }
            }
        }
    }
}