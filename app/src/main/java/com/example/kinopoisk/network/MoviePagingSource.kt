package com.example.kinopoisk.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.MovieResponse
import com.example.kinopoisk.PosterResponse
import com.example.kinopoisk.ReviewResponse
import com.example.kinopoisk.Tune
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviePagingSource<T: Any>(
    private val apiService: ApiService,
    private val type: String? = null,
    private val query: String? = null,
    private val tune: Tune? = null,
    private val reviews: Int? = null,
    private val posters: Int? = null,
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return withContext(Dispatchers.IO) {
            try {
                val nextPageNumber = params.key ?: 1
                val response = when {
                    query != null -> {
                        Log.i(
                            "searchMovies",
                            apiService.searchMovies(nextPageNumber, query = query).toString()
                        )
                        apiService.searchMovies(nextPageNumber, query = query)
                    }

                    tune != null -> {
                        val params = mutableMapOf<String, String>()

                        tune.type.takeIf { it.isNotEmpty() }?.let { params["type"] = it }
                        tune.sortField.takeIf { it.isNotEmpty() }
                            ?.let { params["sortField"] = it; params["sortType"] = "-1" }
                        tune.year.takeIf { it.isNotEmpty() }?.let { params["year"] = it }
                        tune.ageRating.takeIf { it.isNotEmpty() }?.let { params["ageRating"] = it }
                        tune.ratingKp.takeIf { it.isNotEmpty() }?.let { params["rating.kp"] = it }
                        tune.genres.takeIf { it.isNotEmpty() }?.forEachIndexed { _, genre ->
                            params["genres.name"] = genre
                        }
                        tune.countries.takeIf { it.isNotEmpty() }?.forEachIndexed { _, country ->
                            params["countries.name"] = country
                        }
                        Log.i("getTune", apiService.getTune(nextPageNumber, params = params).toString())
                        apiService.getTune(nextPageNumber, params = params)
                    }

                    reviews != null -> {
                        Log.i(
                            "searchMovies",
                            apiService.getReview(nextPageNumber, movieId = reviews).toString()
                        )
                        apiService.getReview(nextPageNumber, movieId = reviews)
                    }

                    posters != null -> {
                        Log.i(
                            "searchMovies",
                            apiService.getPosters(nextPageNumber, movieId = posters).toString()
                        )
                        apiService.getPosters(nextPageNumber, movieId = posters)
                    }
                    else -> apiService.getMovies(nextPageNumber)
                }
                Log.i("response", response.body().toString())

                return@withContext if (response.isSuccessful) {
                    val items = response.body()?.let { body ->
                        when (body) {
                            is MovieResponse -> body.docs
                            is ReviewResponse -> body.docs
                            is PosterResponse -> body.docs
                            else -> emptyList()
                        }
                    } ?: emptyList()

                    LoadResult.Page(
                        data = items as List<T>,
                        prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                        nextKey = if (items.isEmpty()) null else nextPageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception("Error fetching data"))
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }
}
