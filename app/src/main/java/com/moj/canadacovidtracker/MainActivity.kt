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

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var linechart: LineChart
    private var scoreList = ArrayList<Score>()

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
            //TODO: add a field for hospitalized(change_hospitalizations)

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

        linechart = binding.lineChart
        initLineChart()
        setChartValues()
    }

    private fun getNetworkServiceInstance(): NetworkService {
        return NetworkServiceImpl()
    }

    private fun setChartValues() {
        val enteries: ArrayList<Entry> = ArrayList()

        scoreList.add(Score("John1", 12))
        scoreList.add(Score("John2", 1))
        scoreList.add(Score("John3", 2))
        scoreList.add(Score("John4", 6))
        scoreList.add(Score("John5", 3))
        scoreList.add(Score("John6", 7))

        for (i in scoreList.indices) {
            val score = scoreList[i]
            enteries.add(Entry(i.toFloat(), score.score.toFloat()))
        }
        val lineDataSet = LineDataSet(enteries, "")


        /* val xValues = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
         val yValues = listOf(

             BarEntry(4f, 0f),
             BarEntry(3f, 0f),
             BarEntry(5f, 0f),
             BarEntry(1f, 0f),
             BarEntry(2.2f, 0f),
             BarEntry(14f, 0f),
             BarEntry(4.3f, 0f)
         )
         val barDataSet = LineDataSet(yValues, "Week days")
 */

        val data = LineData(lineDataSet)
        binding.lineChart.data = data


    }

    data class Score(
        val name: String,
        val score: Int
    )

    inner class MyAxisFromatter : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < scoreList.size) {
                scoreList[index].name
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
        xAxis.valueFormatter = MyAxisFromatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        //xAxis.labelRotationAngle = +90f

    }
}