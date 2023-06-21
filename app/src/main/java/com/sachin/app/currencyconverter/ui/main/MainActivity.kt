package com.sachin.app.currencyconverter.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.sachin.app.currencyconverter.R
import com.sachin.app.currencyconverter.database.AppDatabase
import com.sachin.app.currencyconverter.network.ExchangeApiClient
import com.sachin.app.currencyconverter.network.Rate
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.statement.bodyAsText
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @OptIn(InternalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            if ((System.currentTimeMillis() - lastUpdateTime.first()) > 60 * 60 * 1000L) {
                runCatching {
                    ExchangeApiClient.getRates().bodyAsText()
                }.onSuccess {
                    val response = JSONObject(it)
                    val rates = response.getJSONObject("rates")
                    val rateList = mutableListOf<Rate>()
                    for (key in rates.keys()) {
                        rateList.add(
                            Rate(
                                code = key,
                                value = rates[key].toString().toDouble()
                            )
                        )
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase(baseContext).countriesDao().insertAll(*rateList.toTypedArray())
                        setLastUpdateTime(System.currentTimeMillis())
                        Log.d("TAG", "onCreate: Rate values updated")
                    }
                }
            } else {
                Log.d("TAG", "onCreate: Last update time is less than 1 hour")
            }
        }
    }
}

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val Context.country1Id: Flow<Int>
    get() = dataStore.data.catch {
        if (it is IOException) {
        } else throw it
    }.map {
        it[country1Key] ?: 1
    }

val Context.country2Id: Flow<Int>
    get() = dataStore.data.catch {
        if (it is IOException) {
        } else throw it
    }.map {
        it[country2Key] ?: 2
    }

fun Context.saveCountry1(id: Int) = CoroutineScope(Dispatchers.IO).launch {
    dataStore.edit {
        it[country1Key] = id
    }
}

fun Context.saveCountry2(id: Int) = CoroutineScope(Dispatchers.IO).launch {
    dataStore.edit {
        it[country2Key] = id
    }
}

val Context.lastUpdateTime: Flow<Long>
    get() = dataStore.data.catch {
        if (it is IOException) {
        } else throw it
    }.map {
        it[lastUpdateTimeKey] ?: 0L
    }


fun Context.setLastUpdateTime(timeInMillis: Long) = CoroutineScope(Dispatchers.IO).launch {
    dataStore.edit {
        it[lastUpdateTimeKey] = timeInMillis
    }
}

private val country1Key = intPreferencesKey("country1")
private val country2Key = intPreferencesKey("country2")
private val lastUpdateTimeKey = longPreferencesKey("lastUpdate")