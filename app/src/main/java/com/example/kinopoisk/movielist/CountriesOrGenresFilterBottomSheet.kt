package com.example.kinopoisk.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import com.example.kinopoisk.dataclasses.TuneSingleton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class CountriesOrGenresFilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var confirmButton: MaterialButton
    private lateinit var countriesRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: CountriesOrGenresAdapter
    private lateinit var ageRadioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val arrayType = arguments?.getString("TYPE")
        val view =
            inflater.inflate(R.layout.countries_or_genries_filter_bottom_sheet, container, false)
        confirmButton = view.findViewById(R.id.confirmButton)
        countriesRecyclerView = view.findViewById(R.id.countriesRecyclerView)
        searchView = view.findViewById(R.id.searchView)
        ageRadioGroup = view.findViewById(R.id.ageRadioGroup)
        adapter = CountriesOrGenresAdapter(
            emptyList(), emptyList()
        )
        if (arrayType == "country") {
            adapter = CountriesOrGenresAdapter(
                resources.getStringArray(R.array.countries).toList(),
                TuneSingleton.selectedCountries
            )
        } else if (arrayType == "genre") {
            adapter = CountriesOrGenresAdapter(
                resources.getStringArray(R.array.genres).toList(),
                TuneSingleton.selectedGenres
            )
        } else {
            countriesRecyclerView.visibility = View.GONE
            searchView.visibility = View.GONE
            ageRadioGroup.visibility = View.VISIBLE
            for (i in 0 until ageRadioGroup.childCount) {
                val radioButton = ageRadioGroup.getChildAt(i) as RadioButton
                if (radioButton.text == TuneSingleton.ageRating + "+") {
                    radioButton.isChecked = true
                    break
                }
            }

        }
        countriesRecyclerView.layoutManager = LinearLayoutManager(context)
        countriesRecyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filterItems(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filterItems(newText)
                return false
            }
        })
        confirmButton.setOnClickListener {
            if (arrayType == "country") {
                TuneSingleton.updateSelectedCountries(adapter.getSelectedCountriesOrGenres())
                dismiss()
            } else if (arrayType == "genre") {
                TuneSingleton.updateSelectedGenres(adapter.getSelectedCountriesOrGenres())
                dismiss()
            } else {
                val radioButtonText = when (ageRadioGroup.checkedRadioButtonId) {
                    R.id.radioButton0 -> "0"
                    R.id.radioButton6 -> "6"
                    R.id.radioButton12 -> "12"
                    R.id.radioButton16 -> "16"
                    R.id.radioButton18 -> "18"
                    else -> ""
                }
                TuneSingleton.ageRating = radioButtonText
                dismiss()

            }
        }
        return view
    }
}
