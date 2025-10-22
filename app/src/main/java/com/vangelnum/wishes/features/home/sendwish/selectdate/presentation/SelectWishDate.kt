package com.vangelnum.wishes.features.home.sendwish.selectdate.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.SmallLoadingIndicator
import com.vangelnum.wishes.features.auth.core.utils.copyKey
import com.vangelnum.wishes.features.auth.core.utils.shareKey
import com.vangelnum.wishes.features.home.generateLightColor
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.data.model.WishKey
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

val ColorSaver = listSaver<Color, Float>(
    save = { listOf(it.red, it.green, it.blue, it.alpha) },
    restore = { Color(it[0], it[1], it[2], it[3]) }
)

@Composable
fun ColumnScope.SelectWishDate(
    keyUiState: UiState<WishKey>,
    currentDateUiState: UiState<DateInfo>,
    onShowRegenerateConfirmationDialogChange: (Boolean) -> Unit,
    onShowCalendarDialogChange: (Boolean) -> Unit,
    onNavigateHolidaysScreen: (String, String, String) -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LazyVerticalGrid(
        GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = CircleShape
            ) {
                SelectionContainer(
                    modifier = Modifier.defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                ) {
                    when (keyUiState) {
                        is UiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    keyUiState.message,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        is UiState.Idle -> {}
                        is UiState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                SmallLoadingIndicator()
                            }
                        }

                        is UiState.Success -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    keyUiState.data.key.take(15),
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    onShowRegenerateConfirmationDialogChange(true)
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_cached_24),
                                        contentDescription = "Change key"
                                    )
                                }
                                IconButton(onClick = {
                                    copyKey(
                                        clipboardManager, keyUiState.data.key, scope, context
                                    )
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_content_copy_24),
                                        contentDescription = "Copy"
                                    )
                                }
                                IconButton(onClick = {
                                    shareKey(keyUiState.data.key, context)
                                }) {
                                    Icon(
                                        Icons.Filled.Share,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        when (currentDateUiState) {
            is UiState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.padding(top = 25.dp))
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadingDotsText(text = stringResource(R.string.loading_current_date))
                }
            }

            is UiState.Success -> {
                val dateInfo = currentDateUiState.data
                val currentDayOfMonth = dateInfo.day
                val daysInMonth =
                    YearMonth.of(dateInfo.year, dateInfo.month).lengthOfMonth()
                val daysRemaining = daysInMonth - currentDayOfMonth + 1
                val daysList = (0 until daysRemaining).toList()

                items(daysList) { day ->
                    val dayOfMonth = currentDayOfMonth + day

                    var randomColor by rememberSaveable(dayOfMonth, stateSaver = ColorSaver) {
                        mutableStateOf(generateLightColor())
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (keyUiState is UiState.Success) {
                                    val dateString =
                                        LocalDate
                                            .of(
                                                dateInfo.year,
                                                dateInfo.month,
                                                dayOfMonth
                                            )
                                            .toString()
                                    onNavigateHolidaysScreen(
                                        dateString,
                                        keyUiState.data.key,
                                        currentDateUiState.data.toString()
                                    )
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = randomColor
                        ),
                        shape = RoundedCornerShape(10)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    dayOfMonth.toString(),
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Text(
                                    Month.of(dateInfo.month)
                                        .getDisplayName(
                                            TextStyle.FULL,
                                            Locale.getDefault()
                                        )
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Button(
                        onClick = {
                            onShowCalendarDialogChange(true)
                        },
                        modifier = Modifier.defaultMinSize(
                            minHeight = OutlinedTextFieldDefaults.MinHeight
                        )
                    ) {
                        Text(stringResource(R.string.another_date))
                    }
                }
            }

            is UiState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = currentDateUiState.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Idle -> {}
        }
    }
}
