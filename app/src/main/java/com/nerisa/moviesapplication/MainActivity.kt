package com.nerisa.moviesapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nerisa.moviesapplication.adapters.GenreAdapter
import com.nerisa.moviesapplication.databinding.ActivityMainBinding
import com.nerisa.moviesapplication.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var genreAdapter: GenreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.setVariable(BR.viewModel, mainActivityViewModel)

        recycleViewGenre.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)

        mainActivityViewModel.mutableLiveDataGenres.observe(this, Observer {
            if(it == null){
                recycleViewGenre.adapter = null
            }else{
                genreAdapter = GenreAdapter(this@MainActivity, it)
                recycleViewGenre.adapter = genreAdapter
            }
        })

        mainActivityViewModel.getMoviesGenres()
    }
}