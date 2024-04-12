package com.example.kinopoisk.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kinopoisk.Movie
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val movieRepository: MovieRepository,
    private val pageSize: Int
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val response = movieRepository.getMovies(page)
            if (response.isSuccessful) {
                val movies = response.body()?.docs ?: emptyList()
                LoadResult.Page(
                    data = movies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (movies.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(IOException("Failed to fetch movies: ${response.message()}"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // This is used for state restoration (e.g., when the user comes back to the app after leaving it).
        // You can return a key corresponding to the currently loaded page if needed.
        return state.anchorPosition
    }
}
