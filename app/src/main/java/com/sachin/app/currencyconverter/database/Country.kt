package com.sachin.app.currencyconverter.database


import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class Country(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @Embedded(prefix = "currency_")
    val currency: Currency,
    val isoAlpha2: String,
    val isoAlpha3: String,
    val isoNumeric: String,
    val name: String
)