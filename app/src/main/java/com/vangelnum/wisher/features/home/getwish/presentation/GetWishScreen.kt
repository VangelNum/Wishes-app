package com.vangelnum.wisher.features.home.getwish.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.home.generateLightColor
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetWishScreen(
    modifier: Modifier = Modifier,
    wishesDateState: UiState<List<WishDatesInfo>>,
    currentDateState: UiState<DateInfo>,
    showSnackbar: (String) -> Unit,
    wishState: UiState<Wish>,
    wishKey: MutableState<String>,
    onEvent: (event: GetWishEvent) -> Unit
) {
    var selectedWishId by remember { mutableStateOf<Int?>(null) }
    var bottomSheetVisible by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(wishKey.value) {
        if (wishKey.value.isNotBlank()) {
            delay(500L)
            onEvent(GetWishEvent.OnGetWishesDates(wishKey.value))
        }
    }

    val currentDate = when (currentDateState) {
        is UiState.Success -> {
            LocalDate.of(
                currentDateState.data.year,
                currentDateState.data.month,
                currentDateState.data.day
            )
        }

        else -> {
            LocalDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDate()
        }
    }

    BottomSheetScaffold(
        containerColor = Color.Transparent,
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
            ) {
                if (bottomSheetVisible && selectedWishId != null && wishKey.value.isNotBlank()) {
                    LaunchedEffect(selectedWishId) {
                        onEvent(GetWishEvent.OnGetWishes(wishKey.value, selectedWishId!!))
                    }
                    GetWishScreenToBottomSheet(
                        state = wishState
                    )
                }
            }
        },
        sheetPeekHeight = 0.dp,
        modifier = modifier
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                WishKeyTextField(wishKey.value, onWishKeyChange = {
                    wishKey.value = it
                })
            }
            when (wishesDateState) {
                is UiState.Loading -> {
                    LoadingScreen(
                        loadingText = "Загружаем даты",
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                is UiState.Success -> {
                    WishDatesContent(
                        wishesDateState,
                        currentDate,
                        showSnackbar,
                        scaffoldState,
                        wishKey.value,
                        onSelectedWishId = { selectedWishId = it },
                        onChangeBottomSheetVisible = { bottomSheetVisible = it }
                    )
                }

                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = wishesDateState.message,
                        textStyle = MaterialTheme.typography.headlineMedium,
                        content = {
                            Image(
                                painter = painterResource(R.drawable.emptystate),
                                contentDescription = "Key not found"
                            )
                        },
                        contentAlignment = Alignment.Center
                    )
                }

                is UiState.Idle -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishDatesContent(
    wishesDateState: UiState.Success<List<WishDatesInfo>>,
    currentDate: LocalDate,
    showSnackbar: (String) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    wishKey: String,
    onSelectedWishId: (Int?) -> Unit,
    onChangeBottomSheetVisible: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    Column {
        if (wishesDateState.data.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Нет пожеланий",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
                Image(
                    painter = painterResource(R.drawable.emptystate),
                    contentDescription = "Empty wish list"
                )
            }
        } else {
            val sortedWishes = remember(wishesDateState.data) {
                wishesDateState.data.sortedBy { LocalDate.parse(it.wishDate) }
            }
            val groupedWishes = remember(sortedWishes) {
                sortedWishes.groupBy {
                    val parsedDate = LocalDate.parse(it.wishDate)
                    parsedDate.year to parsedDate.monthValue
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedWishes.forEach { (yearMonth, wishesForMonth) ->
                    header {
                        val monthName = LocalDate.of(yearMonth.first, yearMonth.second, 1)
                            .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
                        Text(
                            text = monthName.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            },
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(wishesForMonth) { wish ->
                        DateWishItem(wish, currentDate, showSnackbar = { message ->
                            showSnackbar(message)
                        }, key = wishKey, onOpenWish = { key, id ->
                            onSelectedWishId(id)
                            onChangeBottomSheetVisible(true)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun DateWishItem(
    wishInfo: WishDatesInfo,
    currentDate: LocalDate,
    showSnackbar: (String) -> Unit,
    key: String,
    onOpenWish: (key: String, id: Int) -> Unit
) {
    val parsedWishDate = LocalDate.parse(wishInfo.wishDate)
    val parsedOpenDate = LocalDate.parse(wishInfo.openDate)
    val formatter = DateTimeFormatter.ofPattern("d", Locale("ru"))
    val formattedDate = parsedWishDate.format(formatter)
    val openDateFormatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
    val formattedOpenDate = parsedOpenDate.format(openDateFormatter)
    val isFuture = parsedOpenDate.isAfter(currentDate)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isFuture) MaterialTheme.colorScheme.surfaceVariant else generateLightColor(),
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .aspectRatio(1f)
            .clickable {
                if (isFuture) {
                    showSnackbar("Можно будет открыть с $formattedOpenDate")
                } else {
                    onOpenWish(key, wishInfo.id.toInt())
                }
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isFuture) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedDate,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Future Wish",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = formattedDate,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }
    }
}

@Composable
fun WishKeyTextField(wishKey: String, onWishKeyChange: (String) -> Unit) {
    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        ),
        value = wishKey,
        onValueChange = {
            onWishKeyChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        placeholder = {
            Text(
                "Введите ключ",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (wishKey.isNotBlank()) {
                IconButton(
                    onClick = {
                        onWishKeyChange("")
                    }
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "clear text")
                }
            }
        },
        singleLine = true,
        shape = CircleShape
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGetWishScreen() {
    val wishKey = remember {
        mutableStateOf("")
    }
    MaterialTheme {
        GetWishScreen(
            wishesDateState = UiState.Success(
                data = listOf(
                    WishDatesInfo(
                        id = 0,
                        "2025-01-19",
                        "2025-01-17"
                    ),
                    WishDatesInfo(
                        id = 1,
                        "2025-01-18",
                        "2025-01-18"
                    ),
                    WishDatesInfo(
                        id = 2,
                        "2025-01-20",
                        "2025-01-19"
                    ),
                    WishDatesInfo(
                        id = 3,
                        "2025-02-10",
                        "2025-02-09"
                    ),
                    WishDatesInfo(
                        id = 4,
                        "2024-02-15",
                        "2024-02-14"
                    )
                )
            ),
            currentDateState = UiState.Success(
                DateInfo(
                    day = 16,
                    formatted = "2024-02-16T15:22:00+03:00",
                    hour = 15,
                    minute = 22,
                    month = 2,
                    timestamp = 1708086120,
                    timezone = "Europe/Moscow",
                    weekDay = 5,
                    year = 2024
                )
            ),
            showSnackbar = {},
            wishState = UiState.Idle(),
            wishKey = wishKey,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyGetWishScreen() {
    val wishKey = remember {
        mutableStateOf("")
    }
    MaterialTheme {
        GetWishScreen(
            wishesDateState = UiState.Success(emptyList()),
            currentDateState = UiState.Success(
                DateInfo(
                    day = 16,
                    formatted = "2024-02-16T15:22:00+03:00",
                    hour = 15,
                    minute = 22,
                    month = 2,
                    timestamp = 1708086120,
                    timezone = "Europe/Moscow",
                    weekDay = 5,
                    year = 2024
                )
            ),
            showSnackbar = {},
            wishState = UiState.Idle(),
            wishKey = wishKey,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingGetWishScreen() {
    val wishKey = remember {
        mutableStateOf("")
    }
    MaterialTheme {
        GetWishScreen(
            wishesDateState = UiState.Loading(),
            currentDateState = UiState.Success(
                DateInfo(
                    day = 16,
                    formatted = "2024-02-16T15:22:00+03:00",
                    hour = 15,
                    minute = 22,
                    month = 2,
                    timestamp = 1708086120,
                    timezone = "Europe/Moscow",
                    weekDay = 5,
                    year = 2024
                )
            ),
            showSnackbar = {},
            wishState = UiState.Idle(),
            wishKey = wishKey,
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorGetWishScreen() {
    val wishKey = remember {
        mutableStateOf("")
    }
    MaterialTheme {
        GetWishScreen(
            wishesDateState = UiState.Error("Failed to fetch wishes"),
            currentDateState = UiState.Success(
                DateInfo(
                    day = 16,
                    formatted = "2024-02-16T15:22:00+03:00",
                    hour = 15,
                    minute = 22,
                    month = 2,
                    timestamp = 1708086120,
                    timezone = "Europe/Moscow",
                    weekDay = 5,
                    year = 2024
                )
            ),
            showSnackbar = {},
            wishState = UiState.Idle(),
            wishKey = wishKey,
            onEvent = {}
        )
    }
}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}