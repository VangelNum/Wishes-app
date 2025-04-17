package com.vangelnum.wishes.features.home.sendwish.selectholiday.presentation

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
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.features.home.sendwish.selectholiday.data.model.Holiday

@Composable
fun HolidaysScreen(
    modifier: Modifier = Modifier,
    holidaysState: UiState<List<Holiday>>,
    onTryAgainLoadingHolidays: () -> Unit,
    onContinueClick: (holiday: Holiday) -> Unit
) {
    when (holidaysState) {
        is UiState.Error -> {
            Column {
                ErrorScreen(
                    errorMessage = holidaysState.message,
                    onButtonClick = onTryAgainLoadingHolidays,
                    buttonMessage = stringResource(R.string.retry_button),
                    content = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ElevatedButton(
                                onClick = {
                                    onContinueClick(Holiday(""))
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
            LoadingScreen(loadingText = stringResource(R.string.loading_holidays))
        }

        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                        onContinueClick(holiday)
                    })
                }
                item {
                    Button(
                        onClick = {
                            onContinueClick(Holiday(""))
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
        holidaysState = UiState.Success(
            listOf(
                Holiday("Some biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiigbiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig text"),
                Holiday("Christmas")
            )
        ),
        onTryAgainLoadingHolidays = {},
        onContinueClick = { _ ->

        }
    )
}


@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewHolidaysErrorScreen() {
    HolidaysScreen(
        holidaysState = UiState.Error("Не удалось загрузить праздники"),
        onTryAgainLoadingHolidays = {},
        onContinueClick = { _ ->

        }
    )
}
