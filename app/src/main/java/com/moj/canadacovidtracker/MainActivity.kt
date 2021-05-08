package com.moj.canadacovidtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moj.canadacovidtracker.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val networkService = getNetworkServiceInstance()
    private val callBack = object : Callback<DataModel> {
        override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
            val currentResponse = response.body()!!.data.get(0)
            val canadaTotalPopulation: Double = 38020682.00
            binding.changeCase.text = response.body()!!.data.get(0).change_cases.toString()
            binding.changeVaccination.text = response.body()!!.data.get(0).change_vaccinations.toString()
            binding.totalToRecovery.text = ((currentResponse.total_cases / currentResponse.total_recoveries)*100.00).toString()
            binding.vaccinationToPopulation.text =((currentResponse.total_vaccinations/canadaTotalPopulation)*(100)).toString()
        }

        override fun onFailure(call: Call<DataModel>, t: Throwable) {
            binding.changeCase.text = t.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkService.getData(callBack)

    }

    private fun getNetworkServiceInstance(): NetworkService {
        return NetworkServiceImpl()
    }
}