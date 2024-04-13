package com.example.kinopoisk

object TuneSingleton {
    var selectedYear: String = ""
    var selectedRating: String = ""
    var selectedContentType: String = ""
    var selectedSortBy: String = ""
    var ageRating: String = ""
    var selectedCountries: MutableList<String> = mutableListOf()
    var selectedGenres: MutableList<String> = mutableListOf()

    fun updateSelectedCountries(countries: List<String>) {
        selectedCountries.clear()
        selectedCountries.addAll(countries)
    }

    fun updateSelectedGenres(genres: List<String>) {
        selectedGenres.clear()
        selectedGenres.addAll(genres)
    }
}