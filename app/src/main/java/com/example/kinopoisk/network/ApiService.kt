package com.example.kinopoisk.network

import com.example.kinopoisk.Movie
import com.example.kinopoisk.MovieResponse
import com.example.kinopoisk.PosterResponse
import com.example.kinopoisk.ReviewResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<MovieResponse>
    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("query") query: String,
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<MovieResponse>

    @GET("v1.4/movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<Movie>

    @GET("v1.4/review")
    suspend fun getReview(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("movieId") movieId: Int,
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<ReviewResponse>

    @GET("v1.4/image")
    suspend fun getPosters(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("movieId") movieId: Int,
        @Query("type") type: String = "still",
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<PosterResponse>

    suspend fun getTune(
        @Query("") url: String,
        @Query("token") apiKey: String = "WF76VQQ-HQB4P5G-JFJH8DF-CRKDP1M"
    ): Response<MovieResponse>

    companion object {
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl("https://api.kinopoisk.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}