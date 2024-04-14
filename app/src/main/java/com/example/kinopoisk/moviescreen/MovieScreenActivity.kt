package com.example.kinopoisk.moviescreen

import MovieScreenViewModel
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.CountriesOrGenriesFilterBottomSheet
import com.example.kinopoisk.FullDescriptionBottomSheet
import com.example.kinopoisk.FullReviewBottomSheet
import com.example.kinopoisk.MovieListViewModelFactory
import com.example.kinopoisk.Poster
import com.example.kinopoisk.R
import com.example.kinopoisk.Review
import com.example.kinopoisk.RoundedCornerTransformation
import com.example.kinopoisk.movielist.MovieListViewModel
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovieScreenActivity : AppCompatActivity() {
    private lateinit var movieName: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieCountry: TextView
    private lateinit var movieRating: TextView
    private lateinit var movieDuration: TextView
    private lateinit var movieGenre: TextView
    private lateinit var toolBar: MaterialToolbar
    private lateinit var moviePoster: ImageView
    private lateinit var reviewList: RecyclerView
    private lateinit var carouselPoster: RecyclerView
    private lateinit var actorslist: RecyclerView
    private lateinit var shortDescription: TextView
    private lateinit var fullDescription: TextView
    private lateinit var seasonAndSeries: TextView
    private lateinit var ageRating: TextView
    private lateinit var NoInfoActorsCard: MaterialCardView
    private lateinit var NoInfoReviewCard: MaterialCardView
    private lateinit var NoInfoImageCard: MaterialCardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_screen)
        movieName = findViewById(R.id.movieName)
        movieYear = findViewById(R.id.movieYear)
        movieCountry = findViewById(R.id.movieCountry)
        movieRating = findViewById(R.id.movieRating)
        toolBar = findViewById(R.id.topAppBar)
        moviePoster = findViewById(R.id.moviePoster)
        movieGenre = findViewById(R.id.movieGenre)
        fullDescription = findViewById(R.id.fullDescription)
        seasonAndSeries = findViewById(R.id.seasonAndSeries)
        movieDuration = findViewById(R.id.movieDuration)
        reviewList = findViewById(R.id.reviewList)
        carouselPoster = findViewById(R.id.carouselPoster)
        actorslist = findViewById(R.id.actorslist)
        shortDescription = findViewById(R.id.shortDescription)
        ageRating = findViewById(R.id.ageRating)
        NoInfoActorsCard = findViewById(R.id.NoInfoActorsCard)
        NoInfoReviewCard = findViewById(R.id.NoInfoReviewCard)
        NoInfoImageCard = findViewById(R.id.NoInfoImageCard)

        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        actorslist.layoutManager = layoutManager

        carouselPoster.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselPoster)
        val snapHelper1 = LinearSnapHelper()
        snapHelper1.attachToRecyclerView(reviewList)

        val adapterReview = ReviewAdapter(::showBottomSheet)
        val adapterPoster = PosterAdapter()
        val adapterPerson = PersonAdapter()
        carouselPoster.adapter = adapterPoster
        actorslist.adapter = adapterPerson

        reviewList.adapter = adapterReview
        reviewList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val intent = intent
        val id = intent.getIntExtra("EXTRA_ID", -1)
        Log.i("id", id.toString())
        val viewModel =
            ViewModelProvider(
                this,
                MovieListViewModelFactory(MovieRepository(ApiService.create()), id)
            )
                .get(MovieScreenViewModel::class.java)

        toolBar.setNavigationOnClickListener {
            finish()
        }
        Log.i("person", viewModel.movie.value.toString())
        viewModel.movie.observe(this) { movie ->
            movieName.text = movie?.name
            movieCountry.text = movie?.countries?.joinToString { country -> country.name }
            movieGenre.text = movie?.genres?.joinToString { country -> country.name }
            if (movie?.rating?.kp == 0.0) {
                movieRating.text = "Рейтинг формируется"
            } else {
                movieRating.text = movie?.rating?.kp.toString()
            }
            if (movie?.ageRating == "0") {
                ageRating.visibility = View.GONE
            } else {
                ageRating.text = movie?.ageRating + "+"
            }

            shortDescription.text = movie?.shortDescription
            if (movie.description.isNotEmpty()) {
                fullDescription.setOnClickListener {
                    val args = Bundle()
                    args.putString("shortDESCRIPTION", movie?.shortDescription)
                    args.putString("fullDESCRIPTION", movie?.description)
                    val fullDescriptionBottomSheet = FullDescriptionBottomSheet()
                    fullDescriptionBottomSheet.arguments = args
                    fullDescriptionBottomSheet.show(
                        supportFragmentManager,
                        "FullDescriptionBottomSheet"
                    )
                }
            }

            if (movie?.type == "movie") {
                if (movie.movieLength == 0) {
                    movieDuration.visibility = View.GONE
                } else {
                    movieDuration.text = movie.movieLength.toString() + "мин"
                    seasonAndSeries.visibility = View.GONE
                    movieYear.text = movie.year
                }
            } else if (movie?.type == "tv-series") {

                if (movie.seriesLength == "0") {
                    movieDuration.visibility = View.GONE
                } else {
                    movieDuration.text = movie.seriesLength + " мин"
                    movieYear.text =
                        movie.releaseYears.joinToString(separator = " - ") { "${it.start} - ${it.end}" }
                    seasonAndSeries.visibility = View.VISIBLE
                    val totalSeasons = movie.seasonsInfo.lastOrNull()?.number ?: 0
                    val totalEpisodes =
                        movie.seasonsInfo.fold(0) { acc, season -> acc + season.episodesCount }
                    seasonAndSeries.text =
                        totalSeasons.toSeasonsString() + " " + totalEpisodes.toEpisodesString()
                }
            }


//            fullDescription.text = movie?.description

            toolBar.title = movie?.name
            if (movie?.poster?.url != null) {
                Picasso.get()
                    .load(movie.poster.url)
                    .resize(300, 450)
                    .transform(RoundedCornerTransformation())
                    .placeholder(R.drawable.download_icon) // Заглушка, отображаемая во время загрузки
                    .error(R.drawable.stub) // Заглушка, отображаемая при ошибке загрузки ВРЕМЕННАЯ
                    .into(moviePoster)
            } else {
                moviePoster.setImageResource(R.drawable.stub)
            }

            val actors = movie.persons.filter { it.enProfession == "actor" }
            if (actors.isEmpty()) {
                actorslist.visibility = View.GONE
                NoInfoActorsCard.visibility = View.VISIBLE
            } else {
                adapterPerson.submitList(actors)
            }
        }

        viewModel.reviews.observe(this) { pagingData ->
            adapterReview.submitData(lifecycle, pagingData)
//            if (reviewList.isEmpty()) {
//                reviewList.visibility = View.GONE
//                NoInfoReviewCard.visibility = View.VISIBLE
//
//            } else {
//                reviewList.visibility = View.VISIBLE
//                NoInfoReviewCard.visibility = View.GONE
//            }
        }

        viewModel.posters.observe(this) { pagingData ->
            adapterPoster.submitData(lifecycle, pagingData)
//            if (reviewList.isEmpty()) {
//                carouselPoster.visibility = View.GONE
//                NoInfoImageCard.visibility = View.VISIBLE
//            } else {
//                carouselPoster.visibility = View.VISIBLE
//                NoInfoImageCard.visibility = View.GONE
//            }
        }
    }

    private fun Int.toSeasonsString(): String {
        return when {
            this % 10 == 1 && this % 100 != 11 -> "$this сезон"
            this % 10 in 2..4 && (this % 100 < 10 || this % 100 >= 20) -> "$this сезона"
            else -> "$this сезонов"
        }
    }

    private fun Int.toEpisodesString(): String {
        return when {
            this % 10 == 1 && this % 100 != 11 -> "$this серия"
            this % 10 in 2..4 && (this % 100 < 10 || this % 100 >= 20) -> "$this серии"
            else -> "$this серий"
        }
    }

    private fun showBottomSheet(
        author: String?,
        date: Date?,
        type: String?,
        title: String?,
        review: String?
    ) {
        val bottomSheetDialog = FullReviewBottomSheet()
        val args = Bundle()
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
        if (date != null) {
            sdf.format(date)
            args.putString("date", sdf.format(date).toString())
        }
        args.putString("author", author)
        args.putString("type", type)
        args.putString("title", title)
        args.putString("review", review)
        bottomSheetDialog.arguments = args

        bottomSheetDialog.show(supportFragmentManager, "FullReviewBottomSheet")
    }
}