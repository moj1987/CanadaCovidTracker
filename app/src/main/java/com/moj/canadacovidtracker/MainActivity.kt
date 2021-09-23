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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var linechart: LineChart

    private val networkService = getNetworkServiceInstance()

    private val callBack = object : Callback<DataModel> {
        override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
            val lastWeekData = getLastWeekData(response)

            initLineChart(lastWeekData)
            setChartValues(lastWeekData)
            setTodayValues(response)
        }

        override fun onFailure(call: Call<DataModel>, t: Throwable) {
            binding.changeCase.text = t.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkService.getAllData(callBack)
        linechart = binding.lineChart
    }

    private fun getNetworkServiceInstance(): NetworkService {
        return NetworkServiceImpl()
    }

    private fun setTodayValues(response: Response<DataModel>) {
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

    private fun getLastWeekData(response: Response<DataModel>): ArrayList<RequiredInfo> {
        val data = response.body()!!.data.takeLast(Calendar.DAY_OF_WEEK)
        val lastWeekData = ArrayList<RequiredInfo>()
        data.asReversed().forEachIndexed { index, i ->
            val dayOfWeek = getDayOfWeek(data[index].date)
            val changeCases = data[index].change_cases

            lastWeekData.add(RequiredInfo(dayOfWeek, changeCases))
        }
        return lastWeekData
    }

    private fun setChartValues(lastWeekData: ArrayList<RequiredInfo>) {
        val entries: ArrayList<Entry> = ArrayList()

        for (i in lastWeekData.indices) {
            val score = lastWeekData[i]
            entries.add(Entry(i.toFloat(), score.changeCases.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "")

        val data = LineData(lineDataSet)
        binding.lineChart.data = data
    }

    inner class MyAxisFormatter(private val lastWeekData: ArrayList<RequiredInfo>) : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < lastWeekData.size) {
                lastWeekData[index].date
            } else {
                ""
            }
        }
    }

    private fun initLineChart(lastWeekData: ArrayList<RequiredInfo>) {
        linechart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = linechart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        linechart.axisRight.isEnabled = false
        linechart.legend.isEnabled = false
        linechart.description.isEnabled = false

        binding.lineChart.setBackgroundColor(resources.getColor(R.color.purple_200))
        binding.lineChart.animateXY(30, 1000)

        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.valueFormatter = MyAxisFormatter(lastWeekData)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
    }

    private fun getDayOfWeek(date: String): String {
        val formatter = SimpleDateFormat("E", Locale.getDefault())
        val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(date) ?: "1900-01-01"
        return formatter.format(currentDate)
    }
}