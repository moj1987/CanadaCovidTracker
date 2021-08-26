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
            val lastUpdated = response.body()!!.last_updated
            val totalCase = currentResponse.total_cases.toDouble()
            val totalRecoveries = currentResponse.total_recoveries.toDouble()
            val fullyVaccinated = currentResponse.total_vaccinated.toDouble()
            val recoveriesPercentage: Double = totalRecoveries / totalCase * ONE_HUNDRES_PERCENT
            val fullyVaccinatedPercentage: Double = fullyVaccinated / CANADA_POPULATION * ONE_HUNDRES_PERCENT
            //New cases:
            binding.changeCase.text = currentResponse.change_cases.toString()
            //Deaths:
            //TODO: add a field for deaths (change_fatalities)

            //Hospitalized
            //TODO: add a field for hospitalized(change hospitalizations)

            //Doses administered today:
            binding.changeVaccination.text = currentResponse.change_vaccinations.toString()

            //binding.totalToRecoveryPercentage.text = recoveriesPercentage.toString()
            binding.totalToRecoveryPercentage.text = String.format("%.2f", recoveriesPercentage)
            binding.fullyVaccinatedPercentage.text = String.format("%.2f", fullyVaccinatedPercentage)
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