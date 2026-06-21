package com.example.utilityapp.di

import com.example.utilityapp.api.CountryApi
import com.example.utilityapp.data.CountryRepository
import com.example.utilityapp.data.CountryRepositoryImpl
import com.example.utilityapp.viewmodels.CountryViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

// All app dependencies declared here for Koin to inject
val appModules = module {

    // Retrofit instance pointed at countriesnow.space
    single {
        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .baseUrl("https://countriesnow.space/")
            .build()
    }

    // CountryApi created from the Retrofit instance
    single<CountryApi> {
        get<Retrofit>().create(CountryApi::class.java)
    }

    // IO dispatcher for running network calls off the main thread
    single { Dispatchers.IO }

    // Repository injected with API and dispatcher
    single<CountryRepository> {
        CountryRepositoryImpl(get(), get())
    }

    // ViewModel injected with repository — single so the same instance is shared
    // between UtilityScreen, SettingsScreen and MainActivity
    single { CountryViewModel(get()) }

    // Image loader with SVG support and a proper User-Agent header,
    // required because Wikimedia blocks requests without one (HTTP 403)
    single {
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "UtilityApp/1.0 (CP3406 Student Project)")
                    .build()
                chain.proceed(request)
            }
            .build()

        coil.ImageLoader.Builder(androidContext())
            .components {
                add(coil.decode.SvgDecoder.Factory())
            }
            .okHttpClient(okHttpClient)
            .build()
    }
}