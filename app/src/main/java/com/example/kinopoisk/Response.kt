package com.example.kinopoisk

data class MovieResponse(
    val docs: List<Movie>
)

data class ReviewResponse(
    val docs: List<Review>
)

data class PosterResponse(
    val docs: List<Poster>
)