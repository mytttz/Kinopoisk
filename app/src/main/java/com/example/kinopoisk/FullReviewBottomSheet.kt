package com.example.kinopoisk


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FullReviewBottomSheet : BottomSheetDialogFragment() {
    private lateinit var type: ImageView
    private lateinit var userName: TextView
    private lateinit var date: TextView
    private lateinit var title: TextView
    private lateinit var review: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.full_review_bottom_sheet, container, false)
        type = view.findViewById(R.id.type)
        userName = view.findViewById(R.id.userName)
        date = view.findViewById(R.id.date)
        title = view.findViewById(R.id.title)
        review = view.findViewById(R.id.review)
        val typeString = arguments?.getString("type")
        val userNameString = arguments?.getString("author")
        val dateString = arguments?.getString("date")
        val titleString = arguments?.getString("title")
        val reviewString = arguments?.getString("review")

        userName.text = userNameString
        date.text = dateString
        title.text = titleString
        review.text = reviewString
        when (typeString) {
            "Позитивный" -> type.setBackgroundColor(Color.parseColor("#03fc0f"))
            "Нейтральный" -> type.setBackgroundColor(Color.parseColor("#bdbfbe"))
            "Негативный" -> type.setBackgroundColor(Color.parseColor("#fc0303"))
        }

        return view
    }
    companion object {
        const val TAG = "FullReviewBottomSheet"
    }
}
