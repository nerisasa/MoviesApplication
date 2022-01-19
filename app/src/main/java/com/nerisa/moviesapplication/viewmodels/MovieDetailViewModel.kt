package com.nerisa.moviesapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nerisa.moviesapplication.activities.MoviesByGenreActivity
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.models.Genre
import com.nerisa.moviesapplication.models.MoviesByGenre
import com.nerisa.moviesapplication.models.UserReview
import com.nerisa.moviesapplication.repositories.MainActivityRepository
import com.nerisa.moviesapplication.repositories.MovieDetailRepository
import com.nerisa.moviesapplication.repositories.MoviesByGenreRepository
import org.json.JSONArray

class MovieDetailViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    var mutableLiveDataUserReview = MutableLiveData<MutableList<UserReview>>()
    var mutableLiveDataMovieTrailer = MutableLiveData<String>()
    var listUserReview = mutableListOf<UserReview>()

    init {
        mutableLiveDataUserReview = MutableLiveData()
        mutableLiveDataMovieTrailer = MutableLiveData()
        listUserReview = mutableListOf()
    }

    fun getMovieTrailer(movieID: String){
        val movieDetailRepository = MovieDetailRepository()

        movieDetailRepository.requestGET(context, "\n" +
                "http://api.themoviedb.org/3/movie/157336/videos?api_key=" + Constant.token, object: MovieDetailRepository.ResponseHandler {
            override fun onSuccess(message: String) {
                mutableLiveDataMovieTrailer.postValue(message)
            }

            override fun onSuccessArray(response: JSONArray) {
                mutableLiveDataMovieTrailer.postValue("")
            }

            override fun onFailure(errorMessage: String) {
                mutableLiveDataMovieTrailer.postValue(errorMessage)
            }

        }, "getMovieTrailer Error")

    }

    fun getUserReviews(movieID:String, page: String){
        val movieDetailRepository = MovieDetailRepository()

        movieDetailRepository.requestGET(context, "\n" +
                "https://api.themoviedb.org/3/movie/"+ movieID +"/reviews?api_key=" + Constant.token + "&language=en-US&page=" + page, object: MovieDetailRepository.ResponseHandler {
            override fun onSuccess(message: String) {
                mutableLiveDataUserReview.postValue(null)
            }

            override fun onSuccessArray(response: JSONArray) {
                for (i in 0 until response.length()){
                    val jsonObject = response.getJSONObject(i)
                    val userReview = UserReview()

                    userReview.id = jsonObject.getString("id")
                    userReview.author = jsonObject.getString("author")
                    userReview.content = jsonObject.getString("content")
                    userReview.created_at = jsonObject.getString("created_at")
                    userReview.updated_at = jsonObject.getString("updated_at")

                    listUserReview.add(userReview)
                }

                mutableLiveDataUserReview.postValue(listUserReview)
            }

            override fun onFailure(errorMessage: String) {
                mutableLiveDataUserReview.postValue(null)
            }

        }, "getUserReviews Error")

    }

}