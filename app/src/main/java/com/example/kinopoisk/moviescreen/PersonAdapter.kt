package com.example.kinopoisk.moviescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kinopoisk.Person
import com.example.kinopoisk.R
import com.example.kinopoisk.RoundedCornerTransformation
import com.squareup.picasso.Picasso


class PersonAdapter(
) :
    ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actorPhoto: ImageView = itemView.findViewById(R.id.actorPhoto)
        val actorName: TextView = itemView.findViewById(R.id.actorName)
        val description: TextView = itemView.findViewById(R.id.description)

//        init {
//            itemView.setOnClickListener {
//                viewModel.selectedFilm(context, getItem(absoluteAdapterPosition)?.id)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.actor_item, parent, false)
        return PersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.actorName.text = getItem(position)?.name
        holder.description.text= getItem(position)?.description
        Picasso.get()
            .load(getItem(position)?.photo)
            .resize(54, 81)
            .transform(RoundedCornerTransformation())
            .placeholder(R.drawable.download_icon) // Заглушка, отображаемая во время загрузки
            .error(R.drawable.stub) // Заглушка, отображаемая при ошибке загрузки ВРЕМЕННАЯ
            .into(holder.actorPhoto)
    }


    class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(
            oldItem: Person,
            newItem: Person
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Person,
            newItem: Person
        ): Boolean {
            return oldItem == newItem
        }
    }
}