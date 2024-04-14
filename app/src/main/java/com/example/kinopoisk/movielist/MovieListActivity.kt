package com.example.kinopoisk.movielist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import com.example.kinopoisk.TuneListener
import com.example.kinopoisk.dataclasses.Tune
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieListViewModelFactory
import com.example.kinopoisk.network.MovieRepository
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView


class MovieListActivity : AppCompatActivity(), TuneListener {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var searchBarInputText: SearchView
    private lateinit var movieSearchList: RecyclerView
    private lateinit var searchBar: SearchBar
    private lateinit var viewModel: MovieListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        filmRecycler = findViewById(R.id.movieList)
        searchBarInputText = findViewById(R.id.searchBarInputText)
        movieSearchList = findViewById(R.id.movieSearchList)
        searchBar = findViewById(R.id.searchBar)

        viewModel =
            ViewModelProvider(
                this,
                MovieListViewModelFactory(MovieRepository(ApiService.create()))
            )[MovieListViewModel::class.java]

        val adapter = MovieAdapter(this, viewModel)
        val adapterSearch = MovieSearchAdapter(this, viewModel)

        searchBar.setOnMenuItemClickListener {
            val modalBottomSheet = TuneBottomSheet()
            modalBottomSheet.setTuneListener(this)
            modalBottomSheet.show(supportFragmentManager, TuneBottomSheet.TAG)
            return@setOnMenuItemClickListener true
        }


        filmRecycler.adapter = adapter
        filmRecycler.layoutManager = LinearLayoutManager(this)

        viewModel.movies.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        val handler = Handler(Looper.getMainLooper())

        searchBarInputText.editText.addTextChangedListener { _ ->
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                movieSearchList.adapter = adapterSearch
                movieSearchList.layoutManager = LinearLayoutManager(this)
                if (searchBarInputText.editText.text.isNotBlank()) {
                    viewModel.fetchMovies(query = searchBarInputText.editText.text.toString())
                    viewModel.searchedMovies.observe(this) { pagingData ->
                        adapterSearch.submitData(lifecycle, pagingData)
                    }
                } else {
                    viewModel.searchedMovies.observe(this) { _ ->
                        adapterSearch.submitData(lifecycle, PagingData.empty())
                    }
                }
            }, 1000)
        }

    }

    override fun onTuneCreated(tune: Tune) {
        viewModel.fetchMovies(tune = tune)
    }
}