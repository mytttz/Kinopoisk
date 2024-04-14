package com.example.kinopoisk.movielist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R

class CountriesOrGenresAdapter(
    private val originalItems: List<String>,
    private val selectedCountriesOrGenres: List<String>
) : RecyclerView.Adapter<CountriesOrGenresAdapter.ViewHolder>() {

    private var filteredItems: MutableList<String> = originalItems.toMutableList()
    private val selectedCountriesOrGenresList = mutableSetOf<String>()

    fun getSelectedCountriesOrGenres(): List<String> = selectedCountriesOrGenresList.toList()

    fun filterItems(query: String?) {
        if (query.isNullOrEmpty()) {
            filteredItems = originalItems.toMutableList()
        } else {
            filteredItems =
                originalItems.filter { it.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.country_or_genre_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val countryOrGenre = filteredItems[position]
        holder.bind(countryOrGenre)
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(countryOrGenre: String) {
            checkbox.text = countryOrGenre
            checkbox.isChecked = selectedCountriesOrGenres.contains(countryOrGenre)

            selectedCountriesOrGenresList.addAll(selectedCountriesOrGenres)

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCountriesOrGenresList.add(countryOrGenre)
                } else {
                    selectedCountriesOrGenresList.remove(countryOrGenre)
                }
            }
        }
    }
}