package com.example.kinopoisk

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.network.ApiService

class MovieSearchPagingSource(private val apiService: ApiService, private val query: String) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = apiService.searchMovies(nextPageNumber, query = query)

            return if (response.isSuccessful) {
                val movies = response.body()?.docs ?: emptyList()
                LoadResult.Page(
                    data = movies,
                    prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                    nextKey = if (movies.isEmpty()) null else nextPageNumber + 1
                )
            } else {
                LoadResult.Error(Exception("Error fetching data"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }
}
