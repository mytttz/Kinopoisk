package com.example.kinopoisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CountriesOrGenriesFilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var confirmButton: Button
    private lateinit var countriesRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: CountriesOrGenriesAdapter

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

        if (arrayType == "country") {
            adapter =
                CountriesOrGenriesAdapter(resources.getStringArray(R.array.countries).toList())
        } else if (arrayType == "genre") {
            adapter = CountriesOrGenriesAdapter(resources.getStringArray(R.array.genres).toList())
        }
        countriesRecyclerView.layoutManager = LinearLayoutManager(context)
        countriesRecyclerView.adapter = adapter

        confirmButton.setOnClickListener {
            val parentFragment = parentFragment as TuneBottomSheet
            if (arrayType == "country") {
                parentFragment.updateCountries(adapter.getSelectedCountriesOrGenries())
                dismiss()
            } else if (arrayType == "genre") {
                parentFragment.updateGenries(adapter.getSelectedCountriesOrGenries())
                dismiss()
            }
        }

        // Инициализация других элементов и логика фильтрации...

        return view
    }
}
