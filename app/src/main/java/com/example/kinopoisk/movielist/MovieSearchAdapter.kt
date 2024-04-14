package com.example.kinopoisk.movielist

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.Movie
import com.example.kinopoisk.R
import com.example.kinopoisk.RoundedCornerTransformation
import com.example.kinopoisk.moviescreen.MovieScreenActivity
import com.squareup.picasso.Picasso


class MovieSearchAdapter(
    private val context: Context,
    private val viewModel: MovieListViewModel
) :
    PagingDataAdapter <Movie, MovieSearchAdapter.MovieViewHolder>(MovieDiffCallback()) {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieName: TextView = itemView.findViewById(R.id.movieName)
        val movieYear: TextView = itemView.findViewById(R.id.movieYear)
        val movieCounty: TextView = itemView.findViewById(R.id.movieCounty)
        val movieGenre: TextView = itemView.findViewById(R.id.movieGenre)
        val movieRating: TextView = itemView.findViewById(R.id.movieRating)
        val moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)

        init {
            itemView.setOnClickListener {
                viewModel.selectedFilm(context, getItem(absoluteAdapterPosition)?.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.movieName.text = getItem(position)?.name
        holder.movieYear.text= getItem(position)?.year
        holder.movieCounty.text= getItem(position)?.countries?.joinToString { it.name }
        holder.movieGenre.text= getItem(position)?.genres?.joinToString { it.name }
        holder.movieRating.text= getItem(position)?.rating?.kp.toString()
        Picasso.get()
            .load(getItem(position)?.poster?.url)
            .resize(72, 108)
            .transform(RoundedCornerTransformation())
            .placeholder(R.drawable.download_icon) // Заглушка, отображаемая во время загрузки
            .error(R.drawable.stub) // Заглушка, отображаемая при ошибке загрузки ВРЕМЕННАЯ
            .into(holder.moviePoster)
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(
            oldItem: Movie,
            newItem: Movie
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Movie,
            newItem: Movie
        ): Boolean {
            return oldItem == newItem
        }
    }
}