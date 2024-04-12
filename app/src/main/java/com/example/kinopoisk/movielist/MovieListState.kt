package com.example.kinopoisk.movielist

import com.example.kinopoisk.Movie

sealed interface MovieListState {
    data class Content(val items: List<Movie>) : MovieListState
    data object Initial : MovieListState
}