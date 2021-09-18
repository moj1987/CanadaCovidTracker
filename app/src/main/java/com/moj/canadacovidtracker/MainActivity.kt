package com.moj.canadacovidtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.moj.canadacovidtracker.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var linechart: LineChart
    private var lastWeekData = ArrayList<RequiredInfo>()

    private val networkService = getNetworkServiceInstance()
    private val callBack = object : Callback<DataModel> {
        override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
            getLastWeekData(response.body()!!.data.takeLast(SEVEN_DAYS_OF_A_WEEK))
            initLineChart()
            setChartValues()
            val currentDayData = response.body()!!.data.last()
            val lastUpdated = response.body()!!.last_updated
            val totalCase = currentDayData.total_cases.toDouble()
            val totalRecoveries = currentDayData.total_recoveries.toDouble()
            val fullyVaccinated = currentDayData.total_vaccinated.toDouble()
            val recoveriesPercentage: Double = totalRecoveries / totalCase * ONE_HUNDREDS_PERCENT
            val fullyVaccinatedPercentage: Double = fullyVaccinated / CANADA_POPULATION * ONE_HUNDREDS_PERCENT
            //New cases:
            binding.changeCase.text = currentDayData.change_cases.toString()
            //Deaths:
            //TODO: add a field for deaths (change_fatalities)

            //Hospitalized
            //TODO: add a field for hospitalized(change_hospitalizations)

            //Doses administered today:
            binding.changeVaccination.text = currentDayData.change_vaccinations.toString()

            //binding.totalToRecoveryPercentage.text = recoveriesPercentage.toString()
            binding.totalToRecoveryPercentage.text = String.format("%.2f", recoveriesPercentage)
            binding.fullyVaccinatedPercentage.text = String.format("%.2f", fullyVaccinatedPercentage)
        }

        override fun onFailure(call: Call<DataModel>, t: Throwable) {
            binding.changeCase.text = t.toString()
        }
    }

    private fun getLastWeekData(data: List<Info>) {
        val currentDayOfWeek = Calendar.DAY_OF_WEEK


        data.forEachIndexed {index,i->
            lastWeekData.add(RequiredInfo( (Calendar.DATE+index).toString()    , i.change_cases))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // networkService.getData(callBack)
        networkService.getAllData(callBack)
        linechart = binding.lineChart
    }

    private fun getNetworkServiceInstance(): NetworkService {
        return NetworkServiceImpl()
    }

    private fun setChartValues() {
        val enteries: ArrayList<Entry> = ArrayList()

        /* lastWeekData.add(RequiredInfo("John1", 12))
         lastWeekData.add(RequiredInfo("John2", 1))
         lastWeekData.add(RequiredInfo("John3", 2))
         lastWeekData.add(RequiredInfo("John4", 6))
         lastWeekData.add(RequiredInfo("John5", 3))
         lastWeekData.add(RequiredInfo("John6", 7))*/

        for (i in lastWeekData.indices) {
            val score = lastWeekData[i]
            enteries.add(Entry(i.toFloat(), score.change_cases.toFloat()))
        }

        val lineDataSet = LineDataSet(enteries, "")

        val data = LineData(lineDataSet)
        binding.lineChart.data = data
    }

    data class Score(
        val name: String,
        val score: Int
    )

    inner class MyAxisFormatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < lastWeekData.size) {
                lastWeekData[index].latest_date
            } else {
                ""
            }
        }
    }

    private fun initLineChart() {
        linechart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = linechart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        linechart.legend.isEnabled = false
        linechart.description.isEnabled = false

        binding.lineChart.setBackgroundColor(resources.getColor(R.color.purple_200))
        binding.lineChart.animateXY(30, 2000)

        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
    }
val WeekDays = mapOf<>{(1,"Monday"),
    (2,"Tuesday")}
}