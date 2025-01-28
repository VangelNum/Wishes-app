package com.vangelnum.wisher.features.home

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wisher.features.home.getwish.presentation.GetWishEvent
import com.vangelnum.wisher.features.home.getwish.presentation.GetWishScreen
import com.vangelnum.wisher.features.home.sendwish.stage1.wishkey.data.model.WishKey
import com.vangelnum.wisher.features.home.sendwish.stage1.worldtime.data.model.DateInfo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    keyUiState: UiState<WishKey>,
    currentDateUiState: UiState<DateInfo>,
    onGetWishKey: () -> Unit,
    onGetTime: () -> Unit,
    onNavigateHolidaysScreen: (String, String, String) -> Unit,
    wishesDatesState: UiState<List<WishDatesInfo>>,
    showSnackbar: (String) -> Unit,
    wishState: UiState<Wish>,
    onRegenerateKey: () -> Unit,
    onEvent: (GetWishEvent) -> Unit,
    key: String?,
    selectedTab: Int?
) {
    LaunchedEffect(true) {
        onGetWishKey()
        onGetTime()
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedHolidayDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(selectedTab ?: 0) }
    val wishKey = remember {
        mutableStateOf(key ?: "")
    }
    var showRegenerateConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedHolidayDate) {
        if (selectedHolidayDate != null && keyUiState is UiState.Success && currentDateUiState is UiState.Success) {
            onNavigateHolidaysScreen(
                selectedHolidayDate.toString(),
                keyUiState.data.key,
                currentDateUiState.data.toString()
            )
        }
    }

    currentDateUiState.let { state ->
        if (state is UiState.Success) {
            val dateInfo = state.data

            if (showCalendarDialog) {
                val calendar = Calendar.getInstance()
                calendar.set(dateInfo.year, dateInfo.month - 1, dateInfo.day)
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        selectedHolidayDate = LocalDate.of(year, month + 1, dayOfMonth)
                        showCalendarDialog = false
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.minDate = calendar.timeInMillis
                datePickerDialog.setOnDismissListener { showCalendarDialog = false }
                datePickerDialog.show()
            }
        }
    }

    if (showRegenerateConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateConfirmationDialog = false },
            title = { Text("Подтверждение смены ключа") },
            text = { Text("Вы уверены, что хотите изменить ключ? Ваши пожелания не удалятся. Текущий ключ станет недоступен. Пожелания станут доступны только по новому ключу.") },
            confirmButton = {
                TextButton(onClick = {
                    onRegenerateKey()
                    showRegenerateConfirmationDialog = false
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRegenerateConfirmationDialog = false
                }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        FancyIndicatorContainerTabs(
            onTabSelected = { index -> selectedTabIndex = index },
            initialTabIndex = selectedTab ?: 0
        )
        if (selectedTabIndex == 0) {
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
                                    Text(
                                        keyUiState.message,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                                    )
                                }

                                is UiState.Idle -> {}
                                is UiState.Loading -> {
                                    SmallLoadingIndicator()
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
                                            showRegenerateConfirmationDialog = true
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_cached_24),
                                                contentDescription = "Change key"
                                            )
                                        }
                                        IconButton(onClick = {
                                            clipboardManager.setText(
                                                AnnotatedString(keyUiState.data.key)
                                            )
                                            Toast.makeText(
                                                context,
                                                R.string.copied,
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_content_copy_24),
                                                contentDescription = "Copy"
                                            )
                                        }
                                        IconButton(onClick = {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT, keyUiState.data.key
                                                )
                                                type = "text/plain"
                                            }
                                            val shareIntent =
                                                Intent.createChooser(sendIntent, null)
                                            context.startActivity(shareIntent)
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
                            val randomColor = remember(dayOfMonth) { generateLightColor() }
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
                                    showCalendarDialog = true
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
        } else {
            GetWishScreen(
                wishesDateState = wishesDatesState,
                modifier = Modifier,
                currentDateState = currentDateUiState,
                showSnackbar = showSnackbar,
                wishState = wishState,
                wishKey = wishKey,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun LoadingDotsText(text: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val dotCount = (alpha * 3).toInt() + 1

    Text(
        text = text + ".".repeat(dotCount),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

fun generateLightColor(): Color {
    val red = Random.nextInt(150, 256)
    val green = Random.nextInt(150, 256)
    val blue = Random.nextInt(150, 256)
    return Color(red, green, blue)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FancyIndicatorContainerTabs(onTabSelected: (Int) -> Unit, initialTabIndex: Int = 0) { // Added initialTabIndex parameter
    var state by remember { mutableIntStateOf(initialTabIndex) } // Initialize state with initialTabIndex
    val titles = listOf(stringResource(R.string.send_wish), stringResource(R.string.get_wishes))

    Column {
        SecondaryTabRow(
            selectedTabIndex = state,
            indicator = { FancyAnimatedIndicatorWithModifier(state) },
            divider = {},
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .clip(CircleShape)
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        onTabSelected(index)
                    },
                    text = { Text(title) },
                    modifier = Modifier.defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabIndicatorScope.FancyAnimatedIndicatorWithModifier(index: Int) {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        )
    var startAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    var endAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val indicatorColor: Color by animateColorAsState(colors[index % colors.size], label = "")

    Box(
        Modifier
            .tabIndicatorLayout { measurable: Measurable,
                                  constraints: Constraints,
                                  tabPositions: List<TabPosition> ->
                val newStart = tabPositions[index].left
                val newEnd = tabPositions[index].right
                val startAnim =
                    startAnimatable
                        ?: Animatable(newStart, Dp.VectorConverter).also { startAnimatable = it }

                val endAnim =
                    endAnimatable
                        ?: Animatable(newEnd, Dp.VectorConverter).also { endAnimatable = it }

                if (endAnim.targetValue != newEnd) {
                    coroutineScope.launch {
                        endAnim.animateTo(
                            newEnd,
                            animationSpec =
                            if (endAnim.targetValue < newEnd) {
                                spring(dampingRatio = 1f, stiffness = 1000f)
                            } else {
                                spring(dampingRatio = 1f, stiffness = 50f)
                            }
                        )
                    }
                }

                if (startAnim.targetValue != newStart) {
                    coroutineScope.launch {
                        startAnim.animateTo(
                            newStart,
                            animationSpec =
                            if (startAnim.targetValue < newStart) {
                                spring(dampingRatio = 1f, stiffness = 50f)
                            } else {
                                spring(dampingRatio = 1f, stiffness = 1000f)
                            }
                        )
                    }
                }

                val indicatorEnd = endAnim.value.roundToPx()
                val indicatorStart = startAnim.value.roundToPx()

                val placeable =
                    measurable.measure(
                        constraints.copy(
                            maxWidth = indicatorEnd - indicatorStart,
                            minWidth = indicatorEnd - indicatorStart,
                        )
                    )
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.place(indicatorStart, 0)
                }
            }
            .padding(1.dp)
            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
            .drawWithContent {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = CornerRadius(100.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
    )
}

@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewHomeScreen() {
    Box(modifier = Modifier.padding(top = 40.dp)) {
        HomeScreen(
            keyUiState = UiState.Idle(),
            onGetWishKey = {},
            currentDateUiState = UiState.Success(
                DateInfo(
                    day = 27,
                    formatted = "27.12.2024 02:09",
                    hour = 2,
                    minute = 9,
                    month = 12,
                    timestamp = 1735254593755,
                    timezone = "Europe/Moscow",
                    weekDay = 5,
                    year = 2024
                )
            ),
            onGetTime = {},
            onNavigateHolidaysScreen = { _, _, _ ->

            },
            wishesDatesState = UiState.Idle(),
            showSnackbar = {},
            wishState = UiState.Idle(),
            onRegenerateKey = {},
            onEvent = {},
            key = null,
            selectedTab = 1
        )
    }
}