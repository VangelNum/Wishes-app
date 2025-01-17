package com.vangelnum.wisher.features.home.sendwish.stage2.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage2.data.Holiday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

@HiltViewModel
class HolidaysViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Holiday>>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HolidaysEvent) {
        when (event) {
            is HolidaysEvent.GetHolidays -> {
                getHolidays(event.date)
            }
        }
    }

    private fun getHolidays(date: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parts = date.split("-")
                val day = parts[1].removePrefix("0")
                val monthNumber = parts[2].removePrefix("0")
                val url = "https://www.calend.ru/holidays/$day-$monthNumber/"

                val response: String = Jsoup.connect(url).execute().body()
                Log.d("HolidaysViewModel", "Raw response from $url:\n$response")

                val doc: Document = Jsoup.parse(response)
                val holidays = mapHolidays(doc)

                withContext(Dispatchers.Main) {
                    if (holidays.isEmpty()) {
                        _uiState.value = UiState.Error("No holidays found")
                    } else {
                        _uiState.value = UiState.Success(data = holidays)
                    }
                }
            } catch (e: Exception) {
                Log.e("HolidaysViewModel", "Error fetching holidays", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = UiState.Error("Failed to load holidays")
                }
            }
        }
    }

    private fun mapHolidays(document: Document): List<Holiday> {
        val holidaysList = mutableListOf<Holiday>()
        val holidayNameElements = document.select(".block.datesList .block.holidays ul.itemsNet li .caption span.title a")
        holidayNameElements.forEach { element ->
            holidaysList.add(Holiday(element.text()))
        }
        return holidaysList
    }
}