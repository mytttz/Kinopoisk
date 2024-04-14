package com.example.kinopoisk.network

import com.example.kinopoisk.dataclasses.Movie
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("token") apiKey: String = TOKEN
    ): Response<MovieResponse>

    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("query") query: String,
        @Query("token") apiKey: String = TOKEN
    ): Response<MovieResponse>

    @GET("v1.4/movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("token") apiKey: String = TOKEN
    ): Response<Movie>

    @GET("v1.4/review")
    suspend fun getReview(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("movieId") movieId: Int,
        @Query("token") apiKey: String = TOKEN
    ): Response<ReviewResponse>

    @GET("v1.4/image")
    suspend fun getPosters(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("movieId") movieId: Int,
        @Query("type") type: String = "still",
        @Query("token") apiKey: String = TOKEN
    ): Response<PosterResponse>

    @GET("v1.4/movie")
    suspend fun getTune(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @QueryMap params: Map<String, String>,
        @Query("token") apiKey: String = TOKEN
    ): Response<MovieResponse>

    companion object {
        private const val TOKEN = "X-API-KEY"
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.kinopoisk.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}