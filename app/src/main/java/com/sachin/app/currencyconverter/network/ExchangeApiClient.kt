package com.sachin.app.currencyconverter.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*

object ExchangeApiClient {

    private const val API_KEY = "0409e99a49bb41b73cb308a4d39feafe"
    private const val URL = "http://api.exchangeratesapi.io/v1/latest?access_key=$API_KEY"
    private const val TIME_OUT = 60 * 1000 // 1 minute

    private val client = HttpClient(Android) {

        /*install(DefaultRequest) {

        }*/

        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }
    }

    suspend fun getRates(): String = client.get(URL)

    /*suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        unit: String,
        lang: String
    ): WeatherResponse {
        return client.get {
            url(BASE_URL + "weather")
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("units", unit)
            parameter("lang", lang)

        }

    }*/
/*suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        unit: String,
        lang: String
    ): WeatherResponse {
        return client.get {
            url(BASE_URL + "weather")
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("units", unit)
            parameter("lang", lang)

        }

    }*/
}