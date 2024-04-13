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
import com.example.kinopoisk.Poster
import com.example.kinopoisk.Review
import com.example.kinopoisk.Tune
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

    private val _movies = MutableLiveData<List<Movie>>()
    val movieItem: LiveData<List<Movie>> get() = _movies

    private val _moviesSearch = MutableLiveData<List<Movie>>()
    val searchItem: LiveData<List<Movie>> get() = _moviesSearch

    private val _moviesTune = MutableLiveData<List<Movie>>()
    val tuneItem: LiveData<List<Movie>> get() = _moviesTune


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchMovies(page = 1) // Вызываем загрузку фильмов при создании ViewModel
    }

    private fun fetchMovies(page: Int) {
        viewModelScope.launch {
            try {
                val responseMovie = movieRepository.getMovies(page)
                Log.i("responseMovie", movieRepository.getMovies(page).toString())
                _movies.value = responseMovie.body()?.docs
                if (responseMovie.isSuccessful) {
                } else {
                    _error.value = "Failed to fetch movies: ${responseMovie.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun fetchSearchMovies(page: Int, query: String) {
        viewModelScope.launch {
            try {
                val responseSearch = movieRepository.searchMovies(page, query)
                Log.i("fetchSearchMovies", responseSearch.body().toString())
                if (responseSearch.isSuccessful) {
                    _moviesSearch.value = responseSearch.body()?.docs

                } else {
                    _error.value = "Failed to fetch movies: ${responseSearch.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

    fun fetchTuneMovies(tune: Tune) {
        viewModelScope.launch {
            val params = mutableMapOf<String, String>()

// Добавляем параметры в map, если они не пустые
            tune.type.takeIf { it.isNotEmpty() }?.let { params["type"] = it }
            tune.sortField.takeIf { it.isNotEmpty() }
                ?.let { params["sortField"] = it; params["sortType"] = "1" }
            tune.year.takeIf { it.isNotEmpty() }?.let { params["year"] = it }
            tune.ratingKp.takeIf { it.isNotEmpty() }?.let { params["rating.kp"] = it }

// Добавляем жанры, если список не пуст
            tune.genres.takeIf { it.isNotEmpty() }?.forEachIndexed { index, genre ->
                params["genres.name$index"] = genre
            }

// Добавляем страны, если список не пуст
            tune.countries.takeIf { it.isNotEmpty() }?.forEachIndexed { index, country ->
                params["countries.name$index"] = country
            }

            // Преобразуем список параметров в строку запроса и объединяем с помощью символа "&"
//            val endtUrl = params.joinToString("&")
            Log.i("url", params.toString())


            try {
                Log.i("getTune", movieRepository.getTune(params).toString())
                val responseTune = movieRepository.getTune(params)

                if (responseTune.isSuccessful) {
                    _moviesTune.value = responseTune.body()?.docs
                } else {
                    _error.value = "Failed to fetch movies: ${responseTune.message()}"
                }
            } catch (e: Exception) {
                Log.i("Error", "Error occurred: ${e.message}")
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