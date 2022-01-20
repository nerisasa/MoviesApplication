package com.nerisa.moviesapplication.activities

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.nerisa.moviesapplication.BR
import com.nerisa.moviesapplication.R
import com.nerisa.moviesapplication.adapters.UserReviewsAdapter
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.cores.LoadingDialog
import com.nerisa.moviesapplication.databinding.ActivityMovieDetailBinding
import com.nerisa.moviesapplication.models.UserReview
import com.nerisa.moviesapplication.viewmodels.MovieDetailViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.activity_movies_by_genre.*
import com.google.android.youtube.player.YouTubeInitializationResult
import android.widget.Toast

import com.google.android.youtube.player.YouTubePlayerFragment

class MovieDetailActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {

    lateinit var binding: ActivityMovieDetailBinding

    private lateinit var movieDetailViewModel: MovieDetailViewModel
    private lateinit var userReviewsAdapter: UserReviewsAdapter

    lateinit var layoutManager: LinearLayoutManager
    lateinit var loadingDialog: LoadingDialog

    var listAllUserReviews = mutableListOf<UserReview>()

    var totalPage = 1
    var isLoading = false

    var stringTrailer: String = ""
    private val RECOVERY_DIALOG_REQUEST = 1

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            youTubePlayer.cueVideo(stringTrailer)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult: YouTubeInitializationResult
    ) {
        if (youTubeInitializationResult.isUserRecoverableError) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(
                "There was an error initializing the YouTubePlayer (%1\$s)",
                youTubeInitializationResult.toString()
            )
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

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
                            totalPage++
                            movieDetailViewModel.getUserReviews(movieID!!, totalPage.toString())
                        }

                    }
                }
            }
        })

        movieDetailViewModel.mutableLiveDataMovieTrailer.observe(this, Observer {
            if(it.isNotEmpty()){
                stringTrailer = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$it\" frameborder=\"0\" allowfullscreen></iframe>"

                webView.settings.javaScriptEnabled = true
                webView.webChromeClient = object : WebChromeClient(){}
                webView.loadData(stringTrailer, "text/html", "utf-8")

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
                    if(it.isEmpty()){
                        totalPage --
                    }else{
                        listAllUserReviews.addAll(it)
                        userReviewsAdapter.notifyDataSetChanged()
                    }
                }
            }
            loadingDialog.dismissDialog()
        })

        movieDetailViewModel.getMovieTrailer(movieID!!)
    }
}