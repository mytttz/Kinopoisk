package com.example.kinopoisk.movielist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.Countries
import com.example.kinopoisk.Genres
import com.example.kinopoisk.Movie
import com.example.kinopoisk.Poster
import com.example.kinopoisk.R
import com.example.kinopoisk.Rating
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.flow.collectLatest

class MovieListActivity : AppCompatActivity() {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var searchBar: SearchBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        filmRecycler = findViewById(R.id.movieList)
        searchBar = findViewById(R.id.searchBar)


        val viewModel = MovieListViewModel()

        val adapter = MovieAdapter(this, viewModel)
        filmRecycler.adapter = adapter
        filmRecycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenCreated {
            viewModel.movies.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

    }
}