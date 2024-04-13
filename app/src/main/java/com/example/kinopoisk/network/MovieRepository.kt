package com.example.kinopoisk.network

import com.example.kinopoisk.Movie
import com.example.kinopoisk.MovieResponse
import com.example.kinopoisk.PosterResponse
import com.example.kinopoisk.ReviewResponse

import retrofit2.Response

class MovieRepository(private val apiService: ApiService) {

    suspend fun getMovies(page: Int): Response<MovieResponse> {
        return apiService.getMovies(page)
    }

    suspend fun getMovie(id: Int): Response<Movie> {
        return apiService.getMovie(id)
    }

    suspend fun getReview(page: Int, movieId: Int): Response<ReviewResponse> {
        return apiService.getReview(page, movieId = movieId)
    }

    suspend fun getPosters(page: Int, movieId: Int): Response<PosterResponse> {
        return apiService.getPosters(page, movieId = movieId)
    }
    suspend fun searchMovies(page: Int, query: String): Response<MovieResponse> {
        return apiService.searchMovies(page, query = query)
    }
    suspend fun getTune(params: Map<String, String>): Response<MovieResponse> {
        return apiService.getTune(params = params)
    }
}