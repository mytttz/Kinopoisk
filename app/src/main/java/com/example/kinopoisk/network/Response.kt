package com.example.kinopoisk.network

import com.example.kinopoisk.dataclasses.Movie
import com.example.kinopoisk.dataclasses.Poster
import com.example.kinopoisk.dataclasses.Review

data class MovieResponse(
    val docs: List<Movie>
)

data class ReviewResponse(
    val docs: List<Review>
)

data class PosterResponse(
    val docs: List<Poster>
)