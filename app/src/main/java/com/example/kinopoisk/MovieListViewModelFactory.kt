package com.example.kinopoisk

import MovieScreenViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kinopoisk.movielist.MovieListViewModel
import com.example.kinopoisk.network.MovieRepository


class MovieListViewModelFactory(
    private val repository: MovieRepository,
    private val additionalIntValue: Int? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MovieScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieScreenViewModel(repository, additionalIntValue ?: 0) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

