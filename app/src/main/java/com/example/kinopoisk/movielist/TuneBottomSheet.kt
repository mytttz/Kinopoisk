package com.example.kinopoisk.movielist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kinopoisk.R
import com.example.kinopoisk.TuneListener
import com.example.kinopoisk.dataclasses.Tune
import com.example.kinopoisk.dataclasses.TuneSingleton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.RangeSlider

class TuneBottomSheet : BottomSheetDialogFragment() {
    private lateinit var yearsSlider: RangeSlider
    private lateinit var countrySelect: TextView
    private lateinit var ageRating: TextView
    private lateinit var ratingSlider: RangeSlider
    private lateinit var genreSelect: TextView
    private lateinit var contentTypeButtonGroup: MaterialButtonToggleGroup
    private lateinit var sortByButtonGroup: MaterialButtonToggleGroup
    private lateinit var applyFiltersButton: MaterialButton
    private lateinit var resetFiltersButton: MaterialButton
    private lateinit var tuneListener: TuneListener


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tune_bottom_sheet, container, false)
        yearsSlider = view.findViewById(R.id.yearsSlider)
        countrySelect = view.findViewById(R.id.countrySelect)
        ageRating = view.findViewById(R.id.ageRating)
        genreSelect = view.findViewById(R.id.genreSelect)
        ratingSlider = view.findViewById(R.id.ratingSlider)
        contentTypeButtonGroup = view.findViewById(R.id.contentTypeButtonGroup)
        sortByButtonGroup = view.findViewById(R.id.sortByButtonGroup)
        applyFiltersButton = view.findViewById(R.id.applyFiltersButton)
        resetFiltersButton = view.findViewById(R.id.resetFiltersButton)

        yearsSlider.values =
            TuneSingleton.selectedYear.takeIf { it.isNotEmpty() }?.split("-")?.map { it.toFloat() }
                ?: listOf(1895f, 2024f)
        ratingSlider.values = TuneSingleton.selectedRating.takeIf { it.isNotEmpty() }?.split("-")
            ?.map { it.toFloat() } ?: listOf(0f, 10f)


        contentTypeButtonGroup.check(
            when (TuneSingleton.selectedContentType) {
                "movie" -> R.id.movieButton
                "tv-series" -> R.id.seriesButton
                else -> R.id.allButton
            }
        )

        sortByButtonGroup.check(
            when (TuneSingleton.selectedSortBy) {
                "rating.kp" -> R.id.ratingButton
                "votes.kp" -> R.id.popularityButton
                "premiere.world" -> R.id.dateButton
                else -> -1
            }
        )


        val args = Bundle()
        countrySelect.setOnClickListener {
            args.putString("TYPE", "country")
            val countriesFilterBottomSheet = CountriesOrGenresFilterBottomSheet()
            countriesFilterBottomSheet.arguments = args
            countriesFilterBottomSheet.show(childFragmentManager, "CountriesFilterBottomSheet")
        }

        genreSelect.setOnClickListener {
            args.putString("TYPE", "genre")
            val countriesFilterBottomSheet = CountriesOrGenresFilterBottomSheet()
            countriesFilterBottomSheet.arguments = args
            countriesFilterBottomSheet.show(childFragmentManager, "CountriesFilterBottomSheet")
        }


        countrySelect.text = buildString {
            append("По стране: ")
            append(TuneSingleton.selectedCountries.joinToString(", "))
        }
        genreSelect.text = buildString {
            append("По жанру: ")
            append(TuneSingleton.selectedGenres.joinToString(", "))
        }
        ageRating.text = buildString {
            append("По возрастному рейтингу:")
            append(TuneSingleton.ageRating)
        }

        resetFiltersButton.setOnClickListener {
            val tune = Tune(
                type = "",
                year = "",
                sortField = "",
                ratingKp = "",
                genres = emptyList(),
                countries = emptyList(),
                ageRating = ""
            )

            tuneListener.onTuneCreated(tune)

            TuneSingleton.selectedYear = ""
            TuneSingleton.selectedRating = ""
            TuneSingleton.selectedContentType = ""
            TuneSingleton.selectedSortBy = ""
            TuneSingleton.selectedCountries = mutableListOf()
            TuneSingleton.selectedGenres = mutableListOf()
            dismiss()
        }

        ageRating.setOnClickListener {
            args.putString("TYPE", "ageRating")
            val countriesFilterBottomSheet = CountriesOrGenresFilterBottomSheet()
            countriesFilterBottomSheet.arguments = args
            countriesFilterBottomSheet.show(childFragmentManager, "CountriesFilterBottomSheet")
        }
        applyFiltersButton.setOnClickListener {

            val year = yearsSlider.values.map { it.toInt() }.joinToString("-")
            val rating = ratingSlider.values.map { it.toInt() }.joinToString("-")

            val contentType = when (contentTypeButtonGroup.checkedButtonId) {
                R.id.movieButton -> "movie"
                R.id.seriesButton -> "tv-series"
                R.id.allButton -> ""
                else -> ""
            }
            val sortBy = when (sortByButtonGroup.checkedButtonId) {
                R.id.ratingButton -> "rating.kp"
                R.id.popularityButton -> "votes.kp"
                R.id.dateButton -> "premiere.world"
                else -> ""
            }

            val tune = Tune(
                type = contentType,
                year = year,
                sortField = sortBy,
                ratingKp = rating,
                genres = TuneSingleton.selectedGenres,
                countries = TuneSingleton.selectedCountries,
                ageRating = TuneSingleton.ageRating
            )

            tuneListener.onTuneCreated(tune)

            TuneSingleton.selectedYear = year
            TuneSingleton.selectedRating = rating
            TuneSingleton.selectedContentType = contentType
            TuneSingleton.selectedSortBy = sortBy

            dismiss()
        }
        return view
    }

    override fun onResume() {


        countrySelect.text = buildString {
            append("По стране:")
            append(TuneSingleton.selectedCountries.joinToString(", "))
        }
        genreSelect.text = buildString {
            append("По жанру:")
            append(TuneSingleton.selectedGenres.joinToString(", "))
        }
        ageRating.text = buildString {
            append("По возрастному рейтингу:")
            append(TuneSingleton.ageRating)
        }
        super.onResume()
    }

    fun setTuneListener(listener: TuneListener) {
        tuneListener = listener
    }

    companion object {
        const val TAG = "TuneBottomSheet"
    }
}