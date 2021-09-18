@file:Suppress("ArrayInDataClass")

package com.moj.canadacovidtracker


data class DataModel(
    val data: Array<Info>,
    val last_updated: String
)

data class Info
    (
    val latest_date: String,
    val change_cases: Int,
    val change_fatalities: Int,
    val change_tests: Int,
    val change_hospitalizations: Double,
    val change_criticals: Double,
    val change_recoveries: Int,
    val change_vaccinations: Int,
    val change_vaccinated: Int,
    val change_vaccines_distributed: Int,
    val total_cases: Int,
    val total_fatalities: Int,
    val total_tests: Int,
    val total_hospitalizations: Int,
    val total_criticals: Int,
    val total_recoveries: Int,
    val total_vaccinations: Int,
    val total_vaccinated: Int,
    val total_vaccines_distributed: Int
)

data class RequiredInfo(
    val latest_date: String,
    val change_cases: Int,
)
