package com.nerisa.moviesapplication.repositories

import android.content.Context
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivityRepository {

    interface ResponseHandler {
        fun onSuccess(message: String)
        fun onSuccessArray(response: JSONArray)
        fun onFailure(message: String)
    }

    fun requestGET(context: Context, serverURL: String?, responseHandler: ResponseHandler, error500: String): Boolean? {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(serverURL)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    responseHandler.onFailure("Unable to process your request")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call?, response: Response) {
                    var statusCode: Int = response.code()

                    if (statusCode == 400) {
                        val responseBody = response.body()!!.string()
                        responseHandler.onFailure(responseBody)
                    }

                    if (statusCode == 422) {
                        val responseBody = response.body()!!.string()
                        responseHandler.onFailure(responseBody)
                    }

                    if (statusCode == 500) {
                        if (error500 != "") {
                            responseHandler.onFailure(error500)
                        }
                    }

                    if (statusCode == 200) {
                        val responseBody = response.body()!!.string()
                        val jsonObject = JSONObject(responseBody)

                        responseHandler.onSuccessArray(jsonObject.getJSONArray("genres"))
                    }
                }
            })
            return true
        } catch (ex: Exception) {
            // Handle the error
        }
        return false
    }

}