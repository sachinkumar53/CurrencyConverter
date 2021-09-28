package com.sachin.app.currencyconverter.ui.countries

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sachin.app.currencyconverter.R
import com.sachin.app.currencyconverter.databinding.FragmentCountriesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountriesFragment : Fragment(R.layout.fragment_countries) {

    private val binding by viewBinding(FragmentCountriesBinding::bind)
    private val viewModel: CountriesViewModel by viewModels()
    private val adapter = CountriesAdapter()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.countries.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.list.adapter = adapter
        adapter.onCountryClick = { id ->
            setFragmentResult("id", Bundle().apply {
                putInt("result", id)
            })
            binding.searchView.clearFocus()
            findNavController().navigateUp()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchQuery.value = newText ?: ""
                return true
            }
        })
    }

}