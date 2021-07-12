package com.sachin.app.currencyconverter.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sachin.app.currencyconverter.network.Rate
import kotlinx.coroutines.flow.Flow


@Dao
interface CountriesDao {

    @Query("SELECT * FROM country")
    fun getAll(): PagingSource<Int, Country>


    @Query("SELECT * FROM country WHERE name LIKE :code or isoAlpha2 LIKE :code or isoAlpha3 LIKE :code or currency_name LIKE :code or currency_symbol LIKE :code or UPPER(currency_code) LIKE :code")
    fun getAllByCode(code: String): PagingSource<Int, Country>

    @Query("SELECT * FROM country")
    fun getAllCountries(): List<Country>

    @Query("SELECT * FROM country WHERE isoAlpha2 LIKE :code")
    fun findCountryByCode(code: String): Flow<Country?>

    @Query("SELECT * FROM country WHERE id = :id")
    fun findCountryById(id: Int): Flow<Country?>

    @Insert
    fun insertAll(vararg country: Country)


    @Insert
    fun insertAll(vararg rate: Rate)

    @Query("SELECT * FROM rate WHERE code LIKE :code LIMIT 1")
    fun findRateByCode(code: String?): Flow<Rate?>
}