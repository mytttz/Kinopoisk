package com.example.kinopoisk.moviescreen

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import com.example.kinopoisk.RoundedCornerTransformation
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieListViewModelFactory
import com.example.kinopoisk.network.MovieRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MovieScreenActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MovieListViewModelFactory(
                MovieRepository(ApiService.create()),
                intent.getIntExtra("EXTRA_ID", -1)
            )
        )
            .get(MovieScreenViewModel::class.java)
    }
    private lateinit var movieName: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieCountry: TextView
    private lateinit var movieRating: TextView
    private lateinit var movieDuration: TextView
    private lateinit var movieGenre: TextView
    private lateinit var actorsTextView: TextView
    private lateinit var reviewsTextView: TextView
    private lateinit var postersTextView: TextView
    private lateinit var toolBar: MaterialToolbar
    private lateinit var moviePoster: ImageView
    private lateinit var reviewList: RecyclerView
    private lateinit var carouselPoster: RecyclerView
    private lateinit var actorslist: RecyclerView
    private lateinit var shortDescriptionText: TextView
    private lateinit var fullDescription: TextView
    private lateinit var seasonAndSeries: TextView
    private lateinit var ageRatingText: TextView


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
        shortDescriptionText = findViewById(R.id.shortDescription)
        ageRatingText = findViewById(R.id.ageRating)

        actorsTextView = findViewById(R.id.actors)
        reviewsTextView = findViewById(R.id.review)
        postersTextView = findViewById(R.id.image)

        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        actorslist.layoutManager = layoutManager

        carouselPoster.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        CarouselSnapHelper().attachToRecyclerView(carouselPoster)
        LinearSnapHelper().attachToRecyclerView(reviewList)

        val adapterReview = ReviewAdapter(::showBottomSheet)
        val adapterPoster = PosterAdapter()
        val adapterPerson = PersonAdapter()
        carouselPoster.adapter = adapterPoster
        actorslist.adapter = adapterPerson
        reviewList.adapter = adapterReview
        reviewList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewModel.movie.observe(this) { movie ->
            with(movie ?: return@observe) {
                movieName.text = name
                movieCountry.text = countries.joinToString { it.name }
                movieGenre.text = genres.joinToString { it.name }
                movieRating.text =
                    if (rating.kp == 0.0) getString(R.string.rating_formed) else "KP:" + rating.kp.toString()
                ageRatingText.visibility =
                    if (ageRating.isNullOrEmpty()) View.GONE else View.VISIBLE
                ageRatingText.text = ageRating.plus("+")
                shortDescriptionText.visibility =
                    if (shortDescription.isNullOrEmpty()) View.GONE else View.VISIBLE
                shortDescriptionText.text = shortDescription
                if (!description.isNullOrEmpty()) {
                    fullDescription.visibility = View.VISIBLE
                    fullDescription.text = getString(R.string.full_description)
                    fullDescription.setOnClickListener {
                        val args = Bundle().apply {
                            putString("shortDESCRIPTION", shortDescription)
                            putString("fullDESCRIPTION", description)
                        }
                        FullDescriptionBottomSheet().apply {
                            arguments = args
                        }.show(supportFragmentManager, "FullDescriptionBottomSheet")
                    }
                } else {
                    fullDescription.visibility = View.GONE
                }
                if (type == "movie") {
                    movieDuration.visibility = if (movieLength == 0) View.GONE else View.VISIBLE
                    movieDuration.text = if (movieLength == 0) "" else "$movieLength мин"
                    seasonAndSeries.visibility = View.GONE
                    movieYear.text = year
                } else if (type == "tv-series") {
                    movieDuration.visibility = if (seriesLength == "0") View.GONE else View.VISIBLE
                    movieDuration.text = if (seriesLength == "0") "" else "$seriesLength мин"
                    movieYear.text =
                        releaseYears.joinToString(separator = " - ") { "${it.start} - ${it.end}" }
                    seasonAndSeries.visibility = View.VISIBLE
                    val totalSeasons = seasonsInfo.lastOrNull()?.number ?: 0
                    val totalEpisodes =
                        seasonsInfo.fold(0) { acc, season -> acc + season.episodesCount }
                    seasonAndSeries.text = buildString {
                        append(totalSeasons.toSeasonsString())
                        append("\n")
                        append(totalEpisodes.toEpisodesString())
                    }
                }

                toolBar.title = name
                if (poster.url != null) {
                    Picasso.get().load(poster.url).resize(300, 450)
                        .transform(RoundedCornerTransformation())
                        .placeholder(R.drawable.stub).error(R.drawable.stub)
                        .into(moviePoster)
                } else {
                    moviePoster.setImageResource(R.drawable.stub)
                }

                val actors = persons.filter { it.enProfession == "actor" }
                if (actors.isEmpty()) {
                    actorsTextView.text = getString(R.string.no_actor_information)
                    actorslist.visibility = View.GONE
                } else {
                    actorsTextView.text = getString(R.string.actors)
                    adapterPerson.submitList(actors)
                }
            }
        }

        viewModel.reviews.observe(this) { pagingData ->
            adapterReview.submitData(lifecycle, pagingData)
            adapterReview.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading && adapterReview.itemCount == 0) {
                    reviewList.visibility = View.GONE
                    reviewsTextView.text = getString(R.string.no_review_information)
                } else {
                    reviewList.visibility = View.VISIBLE
                    reviewsTextView.text = getString(R.string.reviews)
                }
            }
        }

        viewModel.posters.observe(this) { pagingData ->
            adapterPoster.submitData(lifecycle, pagingData)
            adapterPoster.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading && adapterPoster.itemCount == 0) {
                    carouselPoster.visibility = View.GONE
                    postersTextView.text = getString(R.string.no_image_information)
                } else {
                    carouselPoster.visibility = View.VISIBLE
                    postersTextView.text = getString(R.string.images)
                }
            }
        }
        toolBar.setNavigationOnClickListener {
            finish()
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