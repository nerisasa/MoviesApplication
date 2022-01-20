package com.nerisa.moviesapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nerisa.moviesapplication.activities.MoviesByGenreActivity
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.models.Genre
import com.nerisa.moviesapplication.models.MoviesByGenre
import com.nerisa.moviesapplication.repositories.MainActivityRepository
import com.nerisa.moviesapplication.repositories.MoviesByGenreRepository
import org.json.JSONArray

class MoviesByGenreViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    var mutableLiveDataMoviesByGenre = MutableLiveData<MutableList<MoviesByGenre>>()
    var listMoviesByGenre = mutableListOf<MoviesByGenre>()

    init {
        mutableLiveDataMoviesByGenre = MutableLiveData()
        listMoviesByGenre = mutableListOf()
    }

    fun getMoviesByGenre(genreID: String, page: String){
        val moviesByGenreRepository = MoviesByGenreRepository()
        listMoviesByGenre = mutableListOf()

        moviesByGenreRepository.requestGET(context, "\n" +
                "https://api.themoviedb.org/3/discover/movie?api_key=" + Constant.token + "&language=en-US&with_genres=" + genreID + "&page=" + page, object: MoviesByGenreRepository.ResponseHandler {
            override fun onSuccess(message: String) {
                mutableLiveDataMoviesByGenre.postValue(null)
            }

            override fun onSuccessArray(response: JSONArray) {
                for (i in 0 until response.length()){
                    val jsonObject = response.getJSONObject(i)
                    val moviesByGenre = MoviesByGenre()

                    moviesByGenre.movieID = jsonObject.getString("id")
                    moviesByGenre.moviePoster = jsonObject.getString("poster_path")
                    moviesByGenre.movieTitle = jsonObject.getString("original_title")
                    moviesByGenre.movieOverview = jsonObject.getString("overview")

                    listMoviesByGenre.add(moviesByGenre)
                }

                mutableLiveDataMoviesByGenre.postValue(listMoviesByGenre)
            }

            override fun onFailure(errorMessage: String) {
                mutableLiveDataMoviesByGenre.postValue(null)
            }

        }, "getMoviesByGenre Error")

    }

}