package com.example.kinopoisk.movielist

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.kinopoisk.Movie
import com.example.kinopoisk.moviescreen.MovieScreenActivity
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MoviePagingSource
import com.example.kinopoisk.network.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MovieListViewModel(
) : ViewModel() {

    private val movieRepository = MovieRepository(ApiService.create())


    val movies: Flow<PagingData<Movie>> = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        MoviePagingSource(movieRepository, PAGE_SIZE)
    }.flow

    companion object {
        private const val PAGE_SIZE = 10
    }
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    init {
        fetchMovies(page = 1) // Вызываем загрузку фильмов при создании ViewModel
    }

    private fun fetchMovies(page: Int) {
        viewModelScope.launch {
            try {
                val response = movieRepository.getMovies(page)
                if (response.isSuccessful) {
                } else {
                    _error.value = "Failed to fetch movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun fetchSearchMovies(page: Int, query:String) {
        viewModelScope.launch {
            try {
                val response = movieRepository.searchMovies(page, query)
                Log.i("response", response.body().toString())
                if (response.isSuccessful) {
                } else {
                    _error.value = "Failed to fetch movies: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun selectedFilm(context: Context, id: Int?) {
        val intentFilmScreen = Intent(context, MovieScreenActivity::class.java)
        intentFilmScreen.putExtra("EXTRA_ID", id)
        context.startActivity(intentFilmScreen)
    }
}