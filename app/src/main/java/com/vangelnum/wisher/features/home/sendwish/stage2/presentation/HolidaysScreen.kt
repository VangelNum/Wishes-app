package com.vangelnum.wisher.features.home.sendwish.stage2.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.home.sendwish.stage2.data.model.Holiday

@Composable
fun HolidaysScreen(
    modifier: Modifier = Modifier,
    holidayDate: String,
    key: String,
    currentDate: String,
    holidaysState: UiState<List<Holiday>>,
    onTryAgainLoadingHolidays: () -> Unit,
    onContinueClick: (date: String, key: String, holiday: Holiday, currentDate: String) -> Unit
) {
    when (holidaysState) {
        is UiState.Error -> {
            Column {
                ErrorScreen(
                    errorMessage = holidaysState.message,
                    onButtonClick = onTryAgainLoadingHolidays,
                    buttonMessage = stringResource(R.string.try_again),
                    content = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ElevatedButton(
                                onClick = {
                                    onContinueClick(holidayDate, key, Holiday(""), currentDate)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                            ) {
                                Text(
                                    stringResource(R.string.continue_string),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                )
            }
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(text = stringResource(R.string.loading_holidays))
        }

        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
            ) {
                item {
                    Text(
                        stringResource(R.string.holidays),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                items(holidaysState.data) { holiday ->
                    HolidayItem(holiday.name, onClick = {
                        onContinueClick(holidayDate, key, holiday, currentDate)
                    })
                }
                item {
                    Button(
                        onClick = {
                            onContinueClick(holidayDate, key, Holiday(""), currentDate)
                        },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text(stringResource(R.string.another_holiday))
                    }
                }
            }
        }
    }
}

@Composable
fun HolidayItem(holiday: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = holiday,
                modifier = Modifier
                    .padding(8.dp)
                    .padding(start = 8.dp, end = 8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewHolidaysScreen() {
    HolidaysScreen(
        holidayDate = "27.12.2024 03:09",
        key = "testkey",
        holidaysState = UiState.Success(
            listOf(
                Holiday("Some biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiigbiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig text"),
                Holiday("Christmas")
            )
        ),
        onTryAgainLoadingHolidays = {},
        onContinueClick = { _, _, _, _ ->

        },
        currentDate = ""
    )
}


@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewHolidaysErrorScreen() {
    HolidaysScreen(
        holidayDate = "27.12.2024 03:09",
        key = "testkey",
        holidaysState = UiState.Error("Не удалось загрузить праздники"),
        onTryAgainLoadingHolidays = {},
        onContinueClick = { _, _, _, _ ->

        },
        currentDate = ""
    )
}
