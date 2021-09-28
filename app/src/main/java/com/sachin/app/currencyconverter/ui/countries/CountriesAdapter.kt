package com.sachin.app.currencyconverter.ui.countries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sachin.app.currencyconverter.database.Country
import com.sachin.app.currencyconverter.databinding.CountryItemBinding
import java.util.*

class CountriesAdapter : PagingDataAdapter<Country, CountriesAdapter.ViewHolder>(diffCallback) {
    var onCountryClick: ((id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CountryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: CountryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) = with(binding) {
            countryFlag.load("https://flagcdn.com/h60/${country.isoAlpha2.lowercase()}.png") {
                crossfade(true)
            }
            countryName.text = country.name

            val currency: Pair<String, String> = try {
                val currency = Currency.getInstance(country.currency.code.uppercase())
                Pair(
                    currency.symbol, currency.currencyCode
                )
            } catch (e: Exception) {
                Pair(
                    country.currency.symbol, country.currency.code
                )
            }
            countryCurrency.text = String.format("%s/%s", currency.first, currency.second)

            root.setOnClickListener {
                onCountryClick?.invoke(country.id)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Country>() {
            override fun areItemsTheSame(oldItem: Country, newItem: Country) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Country, newItem: Country) =
                oldItem == newItem

        }
    }
}