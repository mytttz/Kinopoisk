import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kinopoisk.Movie
import com.example.kinopoisk.Poster
import com.example.kinopoisk.Review
import com.example.kinopoisk.Tune
import com.example.kinopoisk.network.MoviePagingSource
import com.example.kinopoisk.network.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//package com.example.kinopoisk.moviescreen
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.example.kinopoisk.Movie
//import com.example.kinopoisk.Poster
//import com.example.kinopoisk.Review
//import com.example.kinopoisk.network.MovieRepository
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch

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


//class MovieScreenViewModel(
//    id: Int
//) : ViewModel() {
//
//    private val movieRepository = MovieRepository(ApiService.create())
//
//    private val _movies = MutableLiveData<Movie>()
//    val movieItem: LiveData<Movie> get() = _movies
//
//    private val _review = MutableLiveData<List<Review>>()
//    val reviewItem: LiveData<List<Review>> get() = _review
//
//    private val _posters = MutableLiveData<List<Poster>>()
//    val posterItem: LiveData<List<Poster>> get() = _posters
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> get() = _error
//
//
//    init {
//        fetchMovie(id = id, page = 1) // Вызываем загрузку фильмов при создании ViewModel
//    }
//
//    private fun fetchMovie(id: Int, page:Int) {
//        viewModelScope.launch {
//            try {
//                val responseMovie = movieRepository.getMovie(id)
//                val responseReview = movieRepository.getReview(page, id)
//                val responsePoster = movieRepository.getPosters(page, id)
//                Log.i("mov111", movieRepository.getMovie(id).toString())
//
//                if (responseMovie.isSuccessful) {
//                    _movies.value = responseMovie.body()
//                    _review.value = responseReview.body()?.docs
//                    _posters.value = responsePoster.body()?.docs
//
//                } else {
//                    _error.value = "Failed to fetch movies: ${responseMovie.message()}"
//                }
//            } catch (e: Exception) {
//                _error.value = "Error occurred: ${e.message}"
//            }
//        }
//    }
//}