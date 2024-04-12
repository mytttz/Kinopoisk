package com.example.kinopoisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CountriesOrGenriesAdapter(private val countryOrGenry: List<String>) :
    RecyclerView.Adapter<CountriesOrGenriesAdapter.ViewHolder>() {

    private val selectedCountries = mutableSetOf<String>()

    fun getSelectedCountriesOrGenries(): List<String> = selectedCountries.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_or_genre_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val countryOrGenry = countryOrGenry[position]
        holder.bind(countryOrGenry)
    }

    override fun getItemCount(): Int = countryOrGenry.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textItem: TextView = itemView.findViewById(R.id.countryOrGenryName)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(countryOrGenry: String) {
            textItem.text = countryOrGenry
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
