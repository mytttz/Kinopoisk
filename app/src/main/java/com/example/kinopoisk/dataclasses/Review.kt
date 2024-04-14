package com.example.kinopoisk.dataclasses

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(
    val id: Int,
    val movieId: Int,
    val title: String,
    val type: String,
    val review: String,
    val author: String,
    val userRating: Int,
    val authorId: Int,
    val date: Date
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
        return sdf.format(date)
    }
}