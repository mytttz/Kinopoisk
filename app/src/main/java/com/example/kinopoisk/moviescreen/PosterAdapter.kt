package com.example.kinopoisk.moviescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.R
import com.example.kinopoisk.RoundedCornerTransformation
import com.example.kinopoisk.dataclasses.Poster
import com.squareup.picasso.Picasso


class PosterAdapter(
) :
    PagingDataAdapter<Poster, PosterAdapter.PosterViewHolder>(PosterDiffCallback()) {

    inner class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val poster: ImageView = itemView.findViewById(R.id.poster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_poster_item, parent, false)
        return PosterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        Picasso.get()
            .load(getItem(position)?.url)
            .resize(360, 240)
            .transform(RoundedCornerTransformation(cornerRadius = 64f))
            .placeholder(R.drawable.stub)
            .error(R.drawable.stub)
            .into(holder.poster)
    }


    class PosterDiffCallback : DiffUtil.ItemCallback<Poster>() {
        override fun areItemsTheSame(
            oldItem: Poster,
            newItem: Poster
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: Poster,
            newItem: Poster
        ): Boolean {
            return oldItem == newItem
        }
    }
}