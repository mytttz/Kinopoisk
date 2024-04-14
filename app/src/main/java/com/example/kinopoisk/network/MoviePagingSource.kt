package com.example.kinopoisk.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.dataclasses.Tune
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviePagingSource<T : Any>(
    private val apiService: ApiService,
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
                        apiService.searchMovies(nextPageNumber, query = query)
                    }

                    tune != null -> {
                        val paramsUrl = mutableMapOf<String, String>()
                        tune.type.takeIf { it.isNotEmpty() }?.let { paramsUrl["type"] = it }
                        tune.sortField.takeIf { it.isNotEmpty() }
                            ?.let { paramsUrl["sortField"] = it; paramsUrl["sortType"] = "-1" }
                        tune.year.takeIf { it.isNotEmpty() }?.let { paramsUrl["year"] = it }
                        tune.ageRating.takeIf { it.isNotEmpty() }
                            ?.let { paramsUrl["ageRating"] = it }
                        tune.ratingKp.takeIf { it.isNotEmpty() }
                            ?.let { paramsUrl["rating.kp"] = it }
                        tune.genres.takeIf { it.isNotEmpty() }?.forEachIndexed { _, genre ->
                            paramsUrl["genres.name"] = genre
                        }
                        tune.countries.takeIf { it.isNotEmpty() }?.forEachIndexed { _, country ->
                            paramsUrl["countries.name"] = country
                        }
                        apiService.getTune(nextPageNumber, params = paramsUrl)
                    }

                    reviews != null -> {
                        apiService.getReview(nextPageNumber, movieId = reviews)
                    }

                    posters != null -> {
                        apiService.getPosters(nextPageNumber, movieId = posters)
                    }

                    else -> apiService.getMovies(nextPageNumber)
                }

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
