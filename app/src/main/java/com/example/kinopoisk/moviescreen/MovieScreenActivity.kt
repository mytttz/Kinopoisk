package com.example.kinopoisk.moviescreen

import MovieScreenViewModel
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.MovieListViewModelFactory
import com.example.kinopoisk.R
import com.example.kinopoisk.RoundedCornerTransformation
import com.example.kinopoisk.movielist.MovieListViewModel
import com.example.kinopoisk.network.ApiService
import com.example.kinopoisk.network.MovieRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.squareup.picasso.Picasso

class MovieScreenActivity : AppCompatActivity() {
    private lateinit var movieName: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieCountry: TextView
    private lateinit var movieRating: TextView
    private lateinit var toolBar: MaterialToolbar
    private lateinit var moviePoster: ImageView
    private lateinit var reviewList: RecyclerView
    private lateinit var carouselPoster: RecyclerView
    private lateinit var actorslist: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_screen)
        movieName = findViewById(R.id.movieName)
        movieYear = findViewById(R.id.movieYear)
        movieCountry = findViewById(R.id.movieCounty)
        movieRating = findViewById(R.id.movieRating)
        toolBar = findViewById(R.id.topAppBar)
        moviePoster = findViewById(R.id.moviePoster)
        reviewList = findViewById(R.id.reviewList)
        carouselPoster = findViewById(R.id.carouselPoster)
        actorslist = findViewById(R.id.actorslist)

        val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false)
        actorslist.layoutManager = layoutManager

        carouselPoster.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselPoster)
        val snapHelper1 = LinearSnapHelper()
        snapHelper1.attachToRecyclerView(reviewList)

        val adapterReview = ReviewAdapter()
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
            ViewModelProvider(this, MovieListViewModelFactory(MovieRepository(ApiService.create()), id))
                .get(MovieScreenViewModel::class.java)


        toolBar.setNavigationOnClickListener {
            finish()
        }
        Log.i("person", viewModel.movie.value.toString())
        viewModel.movie.observe(this) { movie ->
            movieName.text = movie?.name
            movieYear.text = movie?.year
            movieCountry.text = movie?.countries?.joinToString { country -> country.name }
            movieRating.text = movie?.rating?.kp.toString()
            toolBar.title = movie?.name
            Picasso.get()
                .load(movie?.poster?.url)
                .resize(300, 450)
                .transform(RoundedCornerTransformation())
                .placeholder(R.drawable.download_icon) // Заглушка, отображаемая во время загрузки
                .error(R.drawable.tune_icon) // Заглушка, отображаемая при ошибке загрузки ВРЕМЕННАЯ
                .into(moviePoster)

            val actors = movie.persons.filter { it.enProfession == "actor" }
            adapterPerson.submitList(actors)
        }

        viewModel.reviews.observe(this) { pagingData ->
            adapterReview.submitData(lifecycle, pagingData)
        }

        viewModel.posters.observe(this) { pagingData ->
            adapterPoster.submitData(lifecycle, pagingData)
        }
    }
}

//        lifecycleScope.launchWhenCreated {
//            viewModel.movie.collectLatest { pagingData ->
//                Log.i("asd", pagingData.toString())
//                val movie: Movie? = pagingData.map {  }()
//                // Проверяем, что movie не равен null, и заполняем соответствующие TextView информацией о фильме
//                movie?.let {
//                    movieName.text = it.name
//                    movieYear.text = it.year
//                    movieCountry.text = it.countries.joinToString { country -> country.name }
//                    movieRating.text = it.rating.kp.toString() // Используйте нужные вам поля объекта Rating
//                }


//
//Вы можете создать отдельный класс для группировки всех данных, связанных с фильмом. В этом классе вы можете объявить поля для каждого аспекта фильма, таких как список фильмов, список отзывов, список постеров и т. д.
//
//Вот пример того, как вы можете создать такой класс:
//
//kotlin
//Copy code
//data class MovieDetails(
//    val movies: List<Movie>,
//    val reviews: List<Review>,
//    val posters: List<Poster>,
//    val error: String? = null // Поле для хранения ошибки, если что-то пошло не так при получении данных
//)
//Затем в вашей ViewModel вы будете использовать этот класс в качестве типа в LiveData:
//
//kotlin
//Copy code
//private val _movieDetails = MutableLiveData<MovieDetails>()
//val movieDetails: LiveData<MovieDetails> get() = _movieDetails
//Когда вы получаете данные из репозитория, вы создаете объект MovieDetails и устанавливаете его в MutableLiveData:
//
//kotlin
//Copy code
//_movieDetails.value = MovieDetails(
//movies = responseMovie.body()?.docs ?: emptyList(),
//reviews = responseReview.body()?.docs ?: emptyList(),
//posters = responsePoster.body()?.docs ?: emptyList(),
//error = responseMovie.errorBody()?.string() // Пример обработки ошибки
//)
//Теперь в вашем пользовательском интерфейсе вы можете наблюдать за объектом MovieDetails и обновлять интерфейс на основе всех доступных данных:
//
//kotlin
//Copy code
//viewModel.movieDetails.observe(this) { movieDetails ->
//    movieDetails?.let { details ->
//        // Обновление интерфейса на основе данных из MovieDetails
//        val movies = details.movies
//        val reviews = details.reviews
//        val posters = details.posters
//        val error = details.error
//        // Дальнейшая обработка данных
//    }
//}
//Таким образом, вы группируете все данные, связанные с фильмом, в одном объекте и можете легко управлять ими в вашем пользовательском интерфейсе.