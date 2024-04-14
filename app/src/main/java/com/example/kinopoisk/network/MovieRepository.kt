package com.example.kinopoisk.network

import androidx.paging.PagingSource
import com.example.kinopoisk.dataclasses.Movie
import com.example.kinopoisk.dataclasses.Poster
import com.example.kinopoisk.dataclasses.Review
import com.example.kinopoisk.dataclasses.Tune

import retrofit2.Response

class MovieRepository(private val apiService: ApiService) {


    suspend fun getMovie(id: Int): Response<Movie> {
        return apiService.getMovie(id)
    }

    fun getMoviesPagingSource(
        query: String? = null,
        tune: Tune? = null
    ): PagingSource<Int, Movie> {
        return MoviePagingSource(apiService, query, tune)
    }

    fun getReviewPagingSource(
        query: String? = null,
        tune: Tune? = null,
        review: Int
    ): PagingSource<Int, Review> {
        return MoviePagingSource(apiService, query, tune, reviews = review)
    }

    fun getPosterPagingSource(
        query: String? = null,
        tune: Tune? = null,
        posters: Int
    ): PagingSource<Int, Poster> {
        return MoviePagingSource(apiService, query, tune, posters = posters)
    }
}