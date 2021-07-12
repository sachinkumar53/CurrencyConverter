package com.sachin.app.currencyconverter

import androidx.lifecycle.ViewModel
import com.sachin.app.currencyconverter.database.AppDatabase
import com.sachin.app.currencyconverter.database.Country
import com.sachin.app.currencyconverter.network.Rate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(database: AppDatabase) : ViewModel() {

    val id1 = MutableStateFlow(1)
    val id2 = MutableStateFlow(2)

    val country1: Flow<Country?> = id1.flatMapLatest {
        database.countriesDao().findCountryById(it)
    }

    val country2: Flow<Country?> = id2.flatMapLatest {
        database.countriesDao().findCountryById(it)
    }

    val rate1: Flow<Rate?> = country1.flatMapLatest {
        database.countriesDao().findRateByCode(it?.currency?.code)
    }

    val rate2: Flow<Rate?> = country2.flatMapLatest {
        database.countriesDao().findRateByCode(it?.currency?.code)
    }

    val swapFlag = MutableStateFlow(false)
}

