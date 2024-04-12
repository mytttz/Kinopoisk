package com.example.kinopoisk.moviescreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kinopoisk.Movie
import com.example.kinopoisk.Poster
import com.example.kinopoisk.Review
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieRepository
import kotlinx.coroutines.launch


class MovieScreenViewModel(
    id: Int
) : ViewModel() {

    private val movieRepository = MovieRepository(ApiService.create())

    private val _movies = MutableLiveData<Movie>()
    val movieItem: LiveData<Movie> get() = _movies

    private val _review = MutableLiveData<List<Review>>()
    val reviewItem: LiveData<List<Review>> get() = _review

    private val _posters = MutableLiveData<List<Poster>>()
    val posterItem: LiveData<List<Poster>> get() = _posters

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    init {
        fetchMovie(id = id, page = 1) // Вызываем загрузку фильмов при создании ViewModel
    }

    private fun fetchMovie(id: Int, page:Int) {
        viewModelScope.launch {
            try {
                val responseMovie = movieRepository.getMovie(id)
                val responseReview = movieRepository.getReview(page, id)
                val responsePoster = movieRepository.getPosters(page, id)
                Log.i("mov111", movieRepository.getMovie(id).toString())

                if (responseMovie.isSuccessful) {
                    _movies.value = responseMovie.body()
                    _review.value = responseReview.body()?.docs
                    _posters.value = responsePoster.body()?.docs
//                    Log.i("mov", responseMovie.body().toString())
//                    Log.i("mov", responseReview.body().toString())
//                    Log.i("mov", responsePoster.body().toString())
                } else {
                    _error.value = "Failed to fetch movies: ${responseMovie.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred: ${e.message}"
            }
        }
    }

}