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
import com.nerisa.moviesapplication.activities.MoviesByGenreActivity
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.models.Genre
import com.nerisa.moviesapplication.models.MoviesByGenre
import com.nerisa.moviesapplication.models.UserReview
import kotlinx.android.synthetic.main.row_genre.view.*
import kotlinx.android.synthetic.main.row_movie_by_genre.view.*
import kotlinx.android.synthetic.main.row_user_review.view.*

class UserReviewsAdapter(private val context: Context, private val userReviews: MutableList<UserReview>) : RecyclerView.Adapter<UserReviewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_user_review, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userReviews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userReview = userReviews[position]!!

        holder.textAuthor.text = userReview.author
        holder.textContent.text = userReview.content
        holder.textCreatedAt.text = userReview.created_at
        holder.textUpdatedAt.text = userReview.updated_at

    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view){
        val textAuthor: TextView = view.textAuthor
        val textContent: TextView = view.textContent
        val textCreatedAt: TextView = view.textCreatedAt
        val textUpdatedAt: TextView = view.textUpdatedAt
    }

}