package com.example.kinopoisk.moviescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kinopoisk.dataclasses.Movie
import com.example.kinopoisk.dataclasses.Poster
import com.example.kinopoisk.dataclasses.Review
import com.example.kinopoisk.network.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieScreenViewModel(
    private val repository: MovieRepository,
    private val request: Int?
) : ViewModel() {

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie> get() = _movie

    private val _reviews = MutableLiveData<PagingData<Review>>()
    val reviews: LiveData<PagingData<Review>> get() = _reviews

    private val _posters = MutableLiveData<PagingData<Poster>>()
    val posters: LiveData<PagingData<Poster>> get() = _posters

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        request?.let {
            fetchMovie(it)
            fetchReviews(it)
            fetchPosters(it)
        }

    }

    private fun fetchMovie(id: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val response = repository.getMovie(id)
                    if (response.isSuccessful) {
                        _movie.postValue(response.body())
                    } else {
                        _error.postValue("Failed to fetch movie: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _error.postValue("Error occurred: ${e.message}")
            }
        }
    }


    private fun fetchReviews(review: Int) {
        val pagingSource = repository.getReviewPagingSource(review = review)
        val pager = Pager(PagingConfig(pageSize = 10)) {
            pagingSource
        }
        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _reviews.postValue(it)
            }
        }
    }

    private fun fetchPosters(posters: Int) {
        val pagingSource = repository.getPosterPagingSource(posters = posters)
        val pager = Pager(PagingConfig(pageSize = 10)) {
            pagingSource
        }
        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _posters.postValue(it)
            }
        }
    }

}