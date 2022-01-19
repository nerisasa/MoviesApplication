package com.nerisa.moviesapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nerisa.moviesapplication.cores.Constant
import com.nerisa.moviesapplication.models.Genre
import com.nerisa.moviesapplication.repositories.MainActivityRepository
import org.json.JSONArray

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    var mutableLiveDataGenres = MutableLiveData<MutableList<Genre>>()
    var listGenre = mutableListOf<Genre>()

    init {
        mutableLiveDataGenres = MutableLiveData()
        listGenre = mutableListOf()
    }

    fun getMoviesGenres(){
        val mainActivityRepository = MainActivityRepository()

        mainActivityRepository.requestGET(context, "https://api.themoviedb.org/3/genre/movie/list?api_key=" + Constant.token + "&language=en-US", object: MainActivityRepository.ResponseHandler {
            override fun onSuccess(message: String) {
                mutableLiveDataGenres.postValue(null)
            }

            override fun onSuccessArray(response: JSONArray) {
                for (i in 0 until response.length()){
                    val jsonObject = response.getJSONObject(i)
                    val genre = Genre()

                    genre.genreID = jsonObject.getString("id")
                    genre.genreName = jsonObject.getString("name")

                    listGenre.add(genre)
                }

                mutableLiveDataGenres.postValue(listGenre)
            }

            override fun onFailure(errorMessage: String) {
                mutableLiveDataGenres.postValue(null)
            }

        }, "getMoviesGenres Error")

    }

}