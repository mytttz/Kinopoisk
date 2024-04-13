package com.example.kinopoisk

data class Movie(
    val id: Int,
    val name: String,
    val alternativeName: String,
    val year: String,
    val rating: Rating,
    val type: String,
    val logo: Logo,
    val genres: List<Genres>,
    val countries: List<Countries>,
    val poster: Poster,
    val description: String,
    val shortDescription: String,
    val persons: List<Person>
)


data class Rating(
    val kp: Double,
    val imdb: Double,
    val filmCritics: Double,
    val russianFilmCritics: Double
)

data class Genres(
    val name: String
)

data class Countries(
    val name: String
)

data class Poster(
    val url: String,
    val previewUrl: String
)

data class Logo(
    val url: String
)

data class Person(
    val id: Int,
    val photo: String,
    val name: String,
    val description: String,
    val enProfession: String
)

data class Tune(
    val type: String,
    val year: String,
    val sortField: String,
    val ageRating: String,
    val ratingKp: String,
    val genres: List<String>,
    val countries: List<String>
)