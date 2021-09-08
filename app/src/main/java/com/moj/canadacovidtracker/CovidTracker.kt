package com.moj.canadacovidtracker

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface CovidTracker {

        @GET("summary")
        fun getCurrentData() : Call<DataModel>

        @GET("reports")
        fun getAllData() : Call<DataModel>
}