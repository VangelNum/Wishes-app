package com.vangelnum.wishes.features.home

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.home.getwish.data.model.WishDatesInfo
import com.vangelnum.wishes.features.home.getwish.presentation.GetWishEvent
import com.vangelnum.wishes.features.home.getwish.presentation.GetWishScreen
import com.vangelnum.wishes.features.home.sendwish.selectdate.presentation.RegenerateConfirmationDialog
import com.vangelnum.wishes.features.home.sendwish.selectdate.presentation.SelectWishDate
import com.vangelnum.wishes.features.home.sendwish.selectdate.wishkey.data.model.WishKey
import com.vangelnum.wishes.features.home.sendwish.selectdate.worldtime.data.model.DateInfo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    keyUiState: UiState<WishKey>,
    currentDateUiState: UiState<DateInfo>,
    onNavigateHolidaysScreen: (String, String, String) -> Unit,
    wishesDatesState: UiState<List<WishDatesInfo>>,
    wishState: UiState<Wish>,
    onRegenerateKey: () -> Unit,
    onGetWishEvent: (GetWishEvent) -> Unit,
    keyFromHistory: String?,
    selectedTab: Int?,
    wishKeyFromWidget: String?
) {
    val context = LocalContext.current
    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedHolidayDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(selectedTab ?: 0) }
    val wishKeyUserInput = rememberSaveable {
        mutableStateOf(keyFromHistory ?: "")
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
        RegenerateConfirmationDialog(
            onShowRegenerateConfirmationDialogChange = {
                showRegenerateConfirmationDialog = it
            },
            onRegenerateKey = onRegenerateKey
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        FancyIndicatorContainerTabs(
            onTabSelected = { index -> selectedTabIndex = index },
            initialTabIndex = selectedTab ?: 0
        )

        if (selectedTabIndex == 0) {
            SelectWishDate(
                keyUiState = keyUiState,
                currentDateUiState = currentDateUiState,
                onShowRegenerateConfirmationDialogChange = {
                    showRegenerateConfirmationDialog = it
                },
                onShowCalendarDialogChange = {
                    showCalendarDialog = it
                },
                onNavigateHolidaysScreen = onNavigateHolidaysScreen
            )
        } else {
            GetWishScreen(
                wishesDateState = wishesDatesState,
                modifier = Modifier,
                currentDateState = currentDateUiState,
                wishState = wishState,
                wishKey = wishKeyUserInput,
                onEvent = onGetWishEvent,
                wishKeyFromWidget = wishKeyFromWidget
            )
        }
    }
}

fun generateLightColor(): Color {
    val red = Random.nextInt(150, 256)
    val green = Random.nextInt(150, 256)
    val blue = Random.nextInt(150, 256)
    return Color(red, green, blue)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FancyIndicatorContainerTabs(
    onTabSelected: (Int) -> Unit,
    initialTabIndex: Int = 0
) {
    var state by remember { mutableIntStateOf(initialTabIndex) }
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
            onNavigateHolidaysScreen = { _, _, _ ->

            },
            wishesDatesState = UiState.Idle(),
            wishState = UiState.Idle(),
            onRegenerateKey = {},
            onGetWishEvent = {},
            keyFromHistory = null,
            selectedTab = 0,
            wishKeyFromWidget = ""
        )
    }
}