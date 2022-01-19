package com.nerisa.moviesapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.nerisa.moviesapplication.R
import com.nerisa.moviesapplication.activities.MoviesByGenreActivity
import com.nerisa.moviesapplication.models.Genre
import kotlinx.android.synthetic.main.row_genre.view.*

class GenreAdapter(private val context: Context, private val genres: MutableList<Genre>) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_genre, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return genres.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genres[position]!!

        holder.textGenre.text = genre.genreName
        holder.textGenre.setOnClickListener {
            val intent = Intent(context, MoviesByGenreActivity::class.java)
            intent.putExtra("genre_id", genre.genreID)
            intent.putExtra("genre_name", genre.genreName)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view){
        val textGenre: TextView = view.textGenre
    }

}