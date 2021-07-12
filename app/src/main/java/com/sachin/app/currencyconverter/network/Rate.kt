package com.sachin.app.currencyconverter.network

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class Rate(
    val code: String,
    val value: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
