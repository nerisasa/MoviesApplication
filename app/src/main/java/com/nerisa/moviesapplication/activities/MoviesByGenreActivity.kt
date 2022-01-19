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
import com.nerisa.moviesapplication.cores.LoadingDialog
import com.nerisa.moviesapplication.models.MoviesByGenre

class MoviesByGenreActivity : AppCompatActivity() {

    lateinit var binding: ActivityMoviesByGenreBinding

    private lateinit var moviesByGenreViewModel: MoviesByGenreViewModel
    private lateinit var moviesByGenreAdapter: MoviesByGenreAdapter

    lateinit var layoutManager: LinearLayoutManager
    lateinit var loadingDialog: LoadingDialog

    var listAllMoviesByGenre = mutableListOf<MoviesByGenre>()

    var totalPage = 1
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val genreID = intent.getStringExtra("genre_id")
        val genreName = intent.getStringExtra("genre_name")

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movies_by_genre)
        binding.lifecycleOwner = this

        moviesByGenreViewModel = ViewModelProvider(this).get(MoviesByGenreViewModel::class.java)
        binding.setVariable(BR.viewModel, moviesByGenreViewModel)

        textTitle.text = "$genreName Movies Collection"

        layoutManager = LinearLayoutManager(this)
        recycleViewMoviesByGenre.layoutManager = layoutManager
        recycleViewMoviesByGenre.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //check for scroll down
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = moviesByGenreAdapter.itemCount

                    if (!isLoading) {

                        //if at the end of list then get another page
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            loadingDialog.show()

                            totalPage++
                            moviesByGenreViewModel.getMoviesByGenre(genreID!!, totalPage.toString())
                        }

                    }
                }
            }
        })

        moviesByGenreViewModel.mutableLiveDataMoviesByGenre.observe(this, Observer {
            if(it == null){
                recycleViewMoviesByGenre.adapter = null
            }else{
                if(listAllMoviesByGenre.isEmpty()){
                    listAllMoviesByGenre.addAll(it)

                    moviesByGenreAdapter = MoviesByGenreAdapter(this@MoviesByGenreActivity, listAllMoviesByGenre)
                    recycleViewMoviesByGenre.adapter = moviesByGenreAdapter
                }else{
                    listAllMoviesByGenre.addAll(it)
                    moviesByGenreAdapter.notifyDataSetChanged()
                }

            }
            loadingDialog.dismissDialog()
        })

        moviesByGenreViewModel.getMoviesByGenre(genreID!!, totalPage.toString())
    }
}