package com.nerisa.moviesapplication.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nerisa.moviesapplication.BR
import com.nerisa.moviesapplication.R
import com.nerisa.moviesapplication.adapters.GenreAdapter
import com.nerisa.moviesapplication.adapters.MoviesByGenreAdapter
import com.nerisa.moviesapplication.databinding.ActivityMainBinding
import com.nerisa.moviesapplication.databinding.ActivityMoviesByGenreBinding
import com.nerisa.moviesapplication.viewmodels.MainActivityViewModel
import com.nerisa.moviesapplication.viewmodels.MoviesByGenreViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movies_by_genre.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nerisa.moviesapplication.adapters.UserReviewsAdapter
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.cores.LoadingDialog
import com.nerisa.moviesapplication.databinding.ActivityMovieDetailBinding
import com.nerisa.moviesapplication.models.MoviesByGenre
import com.nerisa.moviesapplication.viewmodels.MovieDetailViewModel
import kotlinx.android.synthetic.main.activity_movie_detail.*
import android.webkit.WebSettings

import android.webkit.WebView

import android.webkit.WebViewClient
import com.nerisa.moviesapplication.models.UserReview


class MovieDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityMovieDetailBinding

    private lateinit var movieDetailViewModel: MovieDetailViewModel
    private lateinit var userReviewsAdapter: UserReviewsAdapter

    lateinit var layoutManager: LinearLayoutManager
    lateinit var loadingDialog: LoadingDialog

    var listAllUserReviews = mutableListOf<UserReview>()

    var totalPage = 1
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieID = intent.getStringExtra("movieID")
        val moviePoster = intent.getStringExtra("moviePoster")
        val movieTitle = intent.getStringExtra("movieTitle")
        val movieOverview = intent.getStringExtra("movieOverview")

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        binding.lifecycleOwner = this

        movieDetailViewModel = ViewModelProvider(this).get(MovieDetailViewModel::class.java)
        binding.setVariable(BR.viewModel, movieDetailViewModel)

        Glide.with(this)
            .load(Constant.poster_path + moviePoster)
            .into(imageMoviePoster)

        textMovieTitle.text = movieTitle
        textMovieOverview.text = movieOverview

        layoutManager = LinearLayoutManager(this)
        recycleViewUserReviews.layoutManager = layoutManager
        recycleViewUserReviews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //check for scroll down
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = userReviewsAdapter.itemCount

                    if (!isLoading) {

                        //if at the end of list then get another page
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            loadingDialog.show()

                            totalPage++
                            movieDetailViewModel.getUserReviews(movieID!!, totalPage.toString())
                        }

                    }
                }
            }
        })

        movieDetailViewModel.mutableLiveDataMovieTrailer.observe(this, Observer {
            if(it.isNotEmpty()){
                val videoStr =
                    "<html><body>Promo video<br><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/$it\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

                videoView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return false
                    }
                }
                val ws: WebSettings = videoView.getSettings()
                ws.javaScriptEnabled = true
                videoView.loadData(videoStr, "text/html", "utf-8")

                movieDetailViewModel.getUserReviews(movieID!!, totalPage.toString())
            }
        })

        movieDetailViewModel.mutableLiveDataUserReview.observe(this, Observer {
            if(it == null){
                recycleViewUserReviews.adapter = null
            }else{
                if(listAllUserReviews.isEmpty()){
                    listAllUserReviews.addAll(it)

                    userReviewsAdapter = UserReviewsAdapter(this@MovieDetailActivity, listAllUserReviews)
                    recycleViewUserReviews.adapter = userReviewsAdapter
                }else{
                    listAllUserReviews.addAll(it)
                    userReviewsAdapter.notifyDataSetChanged()
                }

            }
            loadingDialog.dismissDialog()
        })

        movieDetailViewModel.getMovieTrailer(movieID!!)
    }
}