package com.sachin.app.currencyconverter.database


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo

@Keep
data class Currency(
    val code: String,
    val name: String,
    val symbol: String
)