package com.example.kinopoisk.moviescreen

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import com.example.kinopoisk.Review
import com.example.kinopoisk.movielist.MovieListViewModel
import java.util.Date


class ReviewAdapter(private val showBottomSheet: (
    author: String?,
    date: Date?,
    type: String?,
    title: String?,
    review: String?
) -> Unit

) :
    PagingDataAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewType: ImageView = itemView.findViewById(R.id.reviewType)
        val authorName: TextView = itemView.findViewById(R.id.authorName)
        val dateReview: TextView = itemView.findViewById(R.id.dateReview)
        val titleReview: TextView = itemView.findViewById(R.id.titleReview)
        val textReview: TextView = itemView.findViewById(R.id.textReview)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val review = getItem(position)
                    showBottomSheet(
                        review?.author,
                        review?.date,
                        review?.type,
                        review?.title,
                        review?.review
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.authorName.text = getItem(position)?.author
        val date = getItem(position)?.getFormattedDate()
        holder.dateReview.text = date
        holder.textReview.text = getItem(position)?.review
        when (getItem(position)?.title) {
            "" -> {
                holder.titleReview.visibility = View.GONE
                holder.textReview.maxLines = 5
            }
            else -> holder.titleReview.text = getItem(position)?.title
        }
        when (getItem(position)?.type) {
            "Позитивный" -> holder.reviewType.setBackgroundColor(Color.parseColor("#03fc0f"))
            "Нейтральный" -> holder.reviewType.setBackgroundColor(Color.parseColor("#bdbfbe"))
            "Негативный" -> holder.reviewType.setBackgroundColor(Color.parseColor("#fc0303"))
        }
    }


    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(
            oldItem: Review,
            newItem: Review
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Review,
            newItem: Review
        ): Boolean {
            return oldItem == newItem
        }
    }
}