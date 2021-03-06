package com.sachin.app.currencyconverter.ui.main

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.sachin.app.currencyconverter.R
import com.sachin.app.currencyconverter.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.math.hypot


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by viewModels()
    private var chooseId = 0

    private val listener1 = object : TextWatcher {
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.editTextTo.hasFocus()) {
                if (text.isNullOrBlank() || text.startsWith('.')) {
                    binding.editTextFrom.setText("")
                } else {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val from = viewModel.rate2.first()?.value ?: 1.0
                        val to = viewModel.rate1.first()?.value ?: 1.0
                        withContext(Dispatchers.Main) {
                            binding.editTextFrom.setText(
                                convert(
                                    text.trim().toString().toDouble(),
                                    from,
                                    to
                                )
                            )
                        }
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}
    }

    private val listener2 = object : TextWatcher {
        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.editTextFrom.hasFocus()) {
                if (text.isNullOrBlank() || text.startsWith('.')) {
                    binding.editTextTo.setText("")
                } else {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val from = viewModel.rate1.first()?.value ?: 1.0
                        val to = viewModel.rate2.first()?.value ?: 1.0
                        withContext(Dispatchers.Main) {
                            binding.editTextTo.setText(
                                convert(
                                    text.trim().toString().toDouble(),
                                    from,
                                    to
                                )
                            )
                        }
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.country1Id?.asLiveData()?.observe(viewLifecycleOwner) {
            viewModel.id1.value = it
        }

        context?.country2Id?.asLiveData()?.observe(viewLifecycleOwner) {
            viewModel.id2.value = it
        }

        context?.lastUpdateTime?.asLiveData()?.observe(viewLifecycleOwner) {
            binding.subtitle.text =
                String.format("Updated %s", DateUtils.getRelativeTimeSpanString(it))

            binding.subtitle.animate()
                .alpha(0f)
                .setStartDelay(5 * 1000L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        binding.toolbar.setNavigationOnClickListener {
            if (binding.subtitle.alpha == 0f) {
                binding.subtitle.animate()
                    .alpha(1f)
                    .setInterpolator(AccelerateInterpolator())
                    .setStartDelay(0L)
                    .withEndAction {
                        binding.subtitle.animate()
                            .alpha(0f)
                            .setStartDelay(5 * 1000L)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    .start()
            }
        }

        binding.apply {
            editTextTo.showSoftInputOnFocus = false
            editTextFrom.showSoftInputOnFocus = false

            editTextFrom.setOnFocusChangeListener { v, hasFocus ->
                keyboard.inputConnection = if (hasFocus) editTextFrom.onCreateInputConnection(
                    EditorInfo()
                ) else null
            }

            editTextTo.setOnFocusChangeListener { v, hasFocus ->
                keyboard.inputConnection = if (hasFocus) editTextTo.onCreateInputConnection(
                    EditorInfo()
                ) else null
            }

            editTextTo.addTextChangedListener(listener1)
            editTextFrom.addTextChangedListener(listener2)

            editTextTo.requestFocus()

            chooseId1.setOnClickListener {
                chooseId = 1
                setFragmentResultListener("id") { key, bundle ->
                    if (chooseId == 1 && bundle.containsKey("result"))
                        context?.saveCountry1(bundle.getInt("result"))
                    clearFragmentResultListener("id")
                }

                findNavController().navigate(R.id.action_mainFragment_to_countriesFragment)
            }

            chooseId2.setOnClickListener {
                chooseId = 2
                setFragmentResultListener("id") { key, bundle ->
                    if (chooseId == 2 && bundle.containsKey("result"))
                        context?.saveCountry2(bundle.getInt("result"))
                    clearFragmentResultListener("id")
                }
                findNavController().navigate(R.id.action_mainFragment_to_countriesFragment)
            }

            fab.setOnClickListener {
                editTextTo.removeTextChangedListener(listener1)
                editTextFrom.removeTextChangedListener(listener2)

                swap()

                val temp = editTextFrom.text
                editTextFrom.text = editTextTo.text
                editTextTo.text = temp

                editTextFrom.setSelection(editTextFrom.text.length)
                editTextTo.setSelection(editTextTo.text.length)
                showRevealAnimation()

                editTextTo.addTextChangedListener(listener1)
                editTextFrom.addTextChangedListener(listener2)
            }
        }

        viewModel.country1.asLiveData().observe(viewLifecycleOwner) {
            binding.apply {
                val currency: Pair<String?, String?> = try {
                    val currency = Currency.getInstance(it?.currency?.code?.uppercase())
                    Pair(
                        currency.symbol, currency.currencyCode
                    )
                } catch (e: Exception) {
                    Pair(
                        it?.currency?.symbol, it?.currency?.code
                    )
                }

                currencyFrom.text = currency.first
                codeFrom.text = currency.second

                flagFrom.load("https://flagcdn.com/h40/${it?.isoAlpha2?.lowercase()}.png")
            }
        }

        viewModel.country2.asLiveData().observe(viewLifecycleOwner) {
            binding.apply {
                val currency: Pair<String?, String?> = try {
                    val currency = Currency.getInstance(it?.currency?.code?.uppercase())
                    Pair(
                        currency.symbol, currency.currencyCode
                    )
                } catch (e: Exception) {
                    Pair(
                        it?.currency?.symbol, it?.currency?.code
                    )
                }

                currencyTo.text = currency.first
                codeTo.text = currency.second

                flagTo.load("https://flagcdn.com/h40/${it?.isoAlpha2?.lowercase()}.png")
            }
        }

        viewModel.swapFlag.asLiveData().observe(viewLifecycleOwner) {
            binding.apply {
                if (it) {
                    dummyView.setBackgroundColor(requireContext().getColor(R.color.color_theme_2))
                    fab.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.color_theme_1))

                } else {
                    dummyView.setBackgroundColor(requireContext().getColor(R.color.color_theme_1))
                    fab.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.color_theme_2))
                }
            }
        }
    }

    private fun swap() {
        CoroutineScope(Dispatchers.IO).launch{
            val temp = viewModel.id1.first()
            requireContext().saveCountry1(viewModel.id2.first())
            requireContext().saveCountry2(temp)
        }
        viewModel.swapFlag.value = !viewModel.swapFlag.value
    }


    private fun showRevealAnimation() = with(binding) {
        val cx = fab.left + fab.width.div(2)
        val cy = fab.top

        val finalRadius = hypot(dummyView.width.div(2f), dummyView.height.div(2f))
        val initialRadius = fab.width / 2f

        ViewAnimationUtils.createCircularReveal(dummyView, cx, cy, initialRadius, finalRadius)
            .also {
                it.doOnEnd {
                    if (dummyView.background is ColorDrawable) {
                        val color = (dummyView.background as ColorDrawable).color
                        root.setBackgroundColor(color)
                    }
                }
                it.start()
            }

    }

    private fun convert(amount: Double, from: Double, to: Double): String {
        return String.format("%.2f", amount.times(to.div(from)))
    }
}