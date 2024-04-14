package com.example.kinopoisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class CountriesOrGenriesAdapter(
    private val originalItems: List<String>,
    private val selectedCountriesOrGenres: List<String>
) : RecyclerView.Adapter<CountriesOrGenriesAdapter.ViewHolder>() {

    private var filteredItems: MutableList<String> = originalItems.toMutableList()
    private val selectedCountries = mutableSetOf<String>()

    fun getSelectedCountriesOrGenries(): List<String> = selectedCountries.toList()

    fun filterItems(query: String?) {
        if (query.isNullOrEmpty()) {
            filteredItems = originalItems.toMutableList()
        } else {
            filteredItems = originalItems.filter { it.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.country_or_genre_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val countryOrGenry = filteredItems[position]
        holder.bind(countryOrGenry)
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(countryOrGenry: String) {
            checkbox.text = countryOrGenry
            checkbox.isChecked = selectedCountriesOrGenres.contains(countryOrGenry)

            selectedCountries.addAll(selectedCountriesOrGenres)

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCountries.add(countryOrGenry)
                } else {
                    selectedCountries.remove(countryOrGenry)
                }
            }
        }
    }
}
