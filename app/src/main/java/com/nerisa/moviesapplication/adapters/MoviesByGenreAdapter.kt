package com.nerisa.moviesapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nerisa.moviesapplication.R
import com.nerisa.moviesapplication.activities.MovieDetailActivity
import com.nerisa.moviesapplication.activities.MoviesByGenreActivity
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.models.Genre
import com.nerisa.moviesapplication.models.MoviesByGenre
import kotlinx.android.synthetic.main.row_genre.view.*
import kotlinx.android.synthetic.main.row_movie_by_genre.view.*

class MoviesByGenreAdapter(private val context: Context, private val moviesByGenre: MutableList<MoviesByGenre>) : RecyclerView.Adapter<MoviesByGenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_movie_by_genre, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return moviesByGenre.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = moviesByGenre[position]!!

        Glide.with(holder.textMovieTitle.context)
            .load(Constant.poster_path + movie.moviePoster)
            .into(holder.imageMoviePoster)

        holder.textMovieTitle.text = movie.movieTitle
        holder.textMovieTitle.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra("movieID", movie.movieID)
            intent.putExtra("moviePoster", movie.moviePoster)
            intent.putExtra("movieTitle", movie.movieTitle)
            intent.putExtra("movieOverview", movie.movieOverview)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view){
        val imageMoviePoster: ImageView = view.imageMoviePoster
        val textMovieTitle: TextView = view.textMovieTitle
    }

}