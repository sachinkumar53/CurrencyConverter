package com.sachin.app.currencyconverter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sachin.app.currencyconverter.database.CountriesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(countriesDao: CountriesDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    val countries = searchQuery.flatMapLatest {
        if (it.isBlank()) {
            Pager(PagingConfig(pageSize = 20)) {
                countriesDao.getAll()
            }.flow.cachedIn(viewModelScope)
        } else {

            Pager(PagingConfig(pageSize = 20)) {
                countriesDao.getAllByCode(it)
            }.flow.cachedIn(viewModelScope)
        }
    }
}