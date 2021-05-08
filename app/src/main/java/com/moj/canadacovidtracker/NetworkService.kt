package com.moj.canadacovidtracker

import retrofit2.Callback

interface NetworkService {
    fun getData(callback:Callback<DataModel>)

}