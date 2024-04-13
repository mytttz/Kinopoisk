package com.example.kinopoisk.movielist

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import kotlinx.coroutines.flow.collectLatest
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kinopoisk.MovieListViewModelFactory
import com.example.kinopoisk.Tune
import com.example.kinopoisk.TuneBottomSheet
import com.example.kinopoisk.TuneListener
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieRepository
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.launch


class MovieListActivity : AppCompatActivity(), TuneListener {

    private lateinit var filmRecycler: RecyclerView
    private lateinit var searchBarInputText: com.google.android.material.search.SearchView
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

        viewModel = ViewModelProvider(this, MovieListViewModelFactory(MovieRepository(ApiService.create())))
            .get(MovieListViewModel::class.java)

        val adapter = MovieAdapter(this, viewModel)
        val adapterSearch = MovieSearchAdapter(this, viewModel)

        searchBar.setOnMenuItemClickListener{
            val modalBottomSheet = TuneBottomSheet()
            modalBottomSheet.setTuneListener(this)
            modalBottomSheet.show(supportFragmentManager, TuneBottomSheet.TAG)
            return@setOnMenuItemClickListener true
        }

        lifecycleScope.launch {
            viewModel.movies.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }


        filmRecycler.adapter = adapter
        filmRecycler.layoutManager = LinearLayoutManager(this)

//        viewModel.movieItem.observe(this) { movieList ->
//            adapter.submitList(movieList)
//        }
//
//        viewModel.tuneItem.observe(this) { movieTune ->
//            adapter.submitList(movieTune)
//        }


         val handler = Handler()

        searchBarInputText.editText.addTextChangedListener { text ->
            // Отменяем предыдущие отложенные действия
            handler.removeCallbacksAndMessages(null)

            // Запускаем отложенное действие через 1 секунду
            handler.postDelayed({
                movieSearchList.adapter = adapterSearch
                movieSearchList.layoutManager = LinearLayoutManager(this)
                Log.i("adapter", "good")

                if (searchBarInputText.editText.text.isNotBlank()) {
                    viewModel.fetchSearchMovies(1, text.toString())
                    viewModel.searchItem.observe(this) { movieSearch ->
                        adapterSearch.submitList(movieSearch)
                    }
                } else {
                    adapterSearch.submitList(emptyList())
                }
            }, 1000) // Задержка в 1 секунду
        }
    }
    override fun onTuneCreated(tune: Tune) {
        viewModel.fetchTuneMovies(tune)
    }
}
