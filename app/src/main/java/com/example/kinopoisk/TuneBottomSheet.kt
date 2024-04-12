package com.example.kinopoisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.slider.RangeSlider

class TuneBottomSheet : BottomSheetDialogFragment() {
    private lateinit var yearsSlider: RangeSlider
    private lateinit var countrySelect: TextView
    private lateinit var ageRatingSpinner: Spinner
    private lateinit var ratingSlider: RangeSlider
    private lateinit var genreSelect: TextView
    private lateinit var contentTypeButtonGroup: MaterialButtonToggleGroup
    private lateinit var sortByButtonGroup: MaterialButtonToggleGroup
    private lateinit var applyFiltersButton: Button
    private lateinit var tuneListener: TuneListener
    private lateinit var selectedCountries: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tune_bottom_sheet, container, false)
        yearsSlider = view.findViewById(R.id.yearsSlider)
        countrySelect = view.findViewById(R.id.country)
        ageRatingSpinner = view.findViewById(R.id.ageRatingSpinner)
        ratingSlider = view.findViewById(R.id.ratingSlider)
        genreSelect = view.findViewById(R.id.genre)
        contentTypeButtonGroup = view.findViewById(R.id.contentTypeButtonGroup)
        sortByButtonGroup = view.findViewById(R.id.sortByButtonGroup)
        applyFiltersButton = view.findViewById(R.id.applyFiltersButton)
        val args = Bundle()


        countrySelect.setOnClickListener {
            args.putString("TYPE", "country")
            val countriesFilterBottomSheet = CountriesOrGenriesFilterBottomSheet()
            countriesFilterBottomSheet.arguments = args
            countriesFilterBottomSheet.show(childFragmentManager, "CountriesFilterBottomSheet")
        }

        genreSelect.setOnClickListener {
            args.putString("TYPE", "genre")
            val countriesFilterBottomSheet = CountriesOrGenriesFilterBottomSheet()
            countriesFilterBottomSheet.arguments = args
            countriesFilterBottomSheet.show(childFragmentManager, "CountriesFilterBottomSheet")
        }

        applyFiltersButton.setOnClickListener {
            val selectedCountries = mutableListOf<Countries>()
            val selectedGenres = mutableListOf<Genres>()

//            countryCheckBox.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    selectedCountries.add(countryName)
//                } else {
//                    selectedCountries.remove(countryName)
//                }
//            }
//            genreCheckBox.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    selectedGenres.add(genreName)
//                } else {
//                    selectedGenres.remove(genreName)
//                }
//            }
            // Получение значений из всех виджетов
            val year = yearsSlider.values.joinToString("-")
//            val country = countrySpinner.selectedItem.toString()
            val ageRating = ageRatingSpinner.selectedItem.toString()
            val rating = ratingSlider.values.joinToString("-")
            // Получение значений из Segmented Button для типа контента и сортировки
            val contentType = when (contentTypeButtonGroup.checkedButtonId) {
                R.id.movieButton -> "Фильм"
                R.id.seriesButton -> "Сериал"
                R.id.allButton -> "Всё"
                else -> ""
            }
            val sortBy = when (sortByButtonGroup.checkedButtonId) {
                R.id.ratingButton -> "По рейтингу"
                R.id.popularityButton -> "Популярности"
                R.id.dateButton -> "По дате"
                else -> ""
            }

            // Создание экземпляра класса Tune
            val tune = Tune(
                type = contentType,
                year = year,
                sortField = sortBy,
                ratingKp = rating,
                genres = selectedGenres, // Вам нужно получить значения жанров из вашего Spinner жанров
                countries = selectedCountries // Здесь передается выбранная страна
            )

            // Теперь у вас есть экземпляр класса Tune, который вы можете использовать по своему усмотрению
            // Например, передать его в какой-то метод или отправить на сервер
         tuneListener.onTuneCreated(tune)
        }



        return view
    }

    fun setTuneListener(listener: TuneListener) {
        tuneListener = listener
    }

    fun updateCountriesOrGenries(selected: List<String>) {
        selectedCountries = selected.toMutableList()
        // Здесь можно обновить UI на основном экране с учетом выбранных стран
    }

    companion object {
        const val TAG = "TuneBottomSheet"
    }
}