package com.example.kinopoisk.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.Movie
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(private val apiService: ApiService) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = apiService.getMovies(nextPageNumber)

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

