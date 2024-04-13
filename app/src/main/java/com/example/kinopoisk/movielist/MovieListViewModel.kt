package com.example.kinopoisk.movielist

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kinopoisk.Movie
import com.example.kinopoisk.Tune
import com.example.kinopoisk.moviescreen.MovieScreenActivity
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MovieListViewModel(
    private val repository: MovieRepository,
) : ViewModel() {


    private val movieRepository = MovieRepository(ApiService.create())

    private val _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> get() = _movies


//    private val _movies = MutableLiveData<List<Movie>>()
//    val movieItem: LiveData<List<Movie>> get() = _movies


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchMovies() // Вызываем загрузку фильмов при создании ViewModel
    }

    fun fetchMovies(type: String? = null, query: String? = null, tune: Tune? = null) {
        val pagingSource = repository.getMoviesPagingSource(type, query, tune = tune)
        val pager = Pager(PagingConfig(pageSize = 10)) {
            pagingSource
        }

        viewModelScope.launch {
            pager.flow.cachedIn(viewModelScope).collectLatest {
                _movies.postValue(it)
            }
        }
    }

    fun selectedFilm(context: Context, id: Int?) {
        val intentFilmScreen = Intent(context, MovieScreenActivity::class.java)
        intentFilmScreen.putExtra("EXTRA_ID", id)
        context.startActivity(intentFilmScreen)
    }
}

