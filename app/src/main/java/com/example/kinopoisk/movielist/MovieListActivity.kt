package com.example.kinopoisk.movielist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import kotlinx.coroutines.flow.collectLatest
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.search.SearchBar

class MovieListActivity : AppCompatActivity() {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var searchBarInputText: com.google.android.material.search.SearchView
    private lateinit var movieSearchList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        filmRecycler = findViewById(R.id.movieList)
        searchBarInputText = findViewById(R.id.searchBarInputText)
        movieSearchList = findViewById(R.id.movieSearchList)
        val viewModel = MovieListViewModel()
        val adapter = MovieAdapter(this, viewModel)
        val adapterSearch = MovieSearchAdapter(this, viewModel)

        movieSearchList.adapter = adapterSearch
        movieSearchList.layoutManager = LinearLayoutManager(this)


        searchBarInputText.editText.addTextChangedListener { text ->
            Log.i("adapter", "good")
            viewModel.fetchSearchMovies(1, text.toString())
            lifecycleScope.launchWhenCreated {
                viewModel.movies.collectLatest { pagingData ->
                    Log.i("collectLatest", pagingData.toString())
                    adapterSearch.submitData(pagingData)
                }
            }
        }




        filmRecycler.adapter = adapter
        filmRecycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenCreated {
            viewModel.movies.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}