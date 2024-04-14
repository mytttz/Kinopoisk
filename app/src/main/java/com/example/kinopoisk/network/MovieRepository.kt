package com.example.kinopoisk.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.Movie
import com.example.kinopoisk.MovieResponse
import com.example.kinopoisk.Poster
import com.example.kinopoisk.PosterResponse
import com.example.kinopoisk.Review
import com.example.kinopoisk.ReviewResponse
import com.example.kinopoisk.Tune

import retrofit2.Response

class MovieRepository(val apiService: ApiService) {


    suspend fun getMovie(id: Int): Response<Movie> {
        return apiService.getMovie(id)
    }

    fun getMoviesPagingSource(
        type: String? = null,
        query: String? = null,
        tune: Tune? = null
    ): PagingSource<Int, Movie> {
        return MoviePagingSource(apiService, type, query, tune)
    }

    fun getReviewPagingSource(
        type: String? = null,
        query: String? = null,
        tune: Tune? = null,
        review: Int
    ): PagingSource<Int, Review> {
        return MoviePagingSource(apiService, type, query, tune, reviews = review)
    }

    fun getPosterPagingSource(
        type: String? = null,
        query: String? = null,
        tune: Tune? = null,
        posters: Int
    ): PagingSource<Int, Poster> {
        return MoviePagingSource(apiService, type, query, tune, posters = posters)
    }
}