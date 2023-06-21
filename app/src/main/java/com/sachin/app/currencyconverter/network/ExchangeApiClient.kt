package com.sachin.app.currencyconverter.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

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

    suspend fun getRates(): HttpResponse {
        return client.get(urlString = URL)
    }
}