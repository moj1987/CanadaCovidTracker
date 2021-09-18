package com.moj.canadacovidtracker

import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkServiceImpl : NetworkService {
    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(CovidTracker::class.java)

    override fun getData(callback: Callback<DataModel>) {
        val call = retrofit.getCurrentData()
        call.enqueue(callback)
    }

   override fun getAllData(callback: Callback<DataModel>) {
        val call = retrofit.getAllData()
        call.enqueue(callback)
    }

    companion object {
        private const val BASE_URL = "https://api.covid19tracker.ca/"
    }
}