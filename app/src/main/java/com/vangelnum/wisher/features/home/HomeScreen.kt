package com.vangelnum.wisher.features.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeKeyUiState: UiState<WishKey>,
    onGetWishKey: () -> Unit
) {
    LaunchedEffect(true) {
        onGetWishKey()
    }

    val currentDate = remember { LocalDate.now() }
    val daysRemaining = remember(currentDate) {
        val lastDayOfMonth = YearMonth.from(currentDate).atEndOfMonth()
        val daysRemaining = lastDayOfMonth.dayOfYear - currentDate.dayOfYear + 1
        daysRemaining
    }

    val daysList = remember(daysRemaining) {
        (1..daysRemaining).toList()
    }

    LazyVerticalGrid(
        GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.height(40.dp)
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            FancyIndicatorContainerTabs()
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            ) {
                SelectionContainer(
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                ) {
                    when (homeKeyUiState) {
                        is UiState.Error -> {
                            Text(
                                homeKeyUiState.message,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            )
                        }

                        UiState.Idle -> {}
                        UiState.Loading -> {
                            SmallLoadingIndicator()
                        }

                        is UiState.Success -> {
                            Text(
                                homeKeyUiState.data.key,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        items(daysList) { day ->
            val dayOfMonth = currentDate.dayOfMonth + day - 1
            val randomColor = remember(day) { generateLightColor() }
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = randomColor
                ),
                shape = RoundedCornerShape(10)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f), contentAlignment = Alignment.Center
                ) {
                    Text(
                        dayOfMonth.toString(),
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        }
    }
}

fun generateLightColor(): Color {
    val red = Random.nextInt(150, 256)
    val green = Random.nextInt(150, 256)
    val blue = Random.nextInt(150, 256)
    return Color(red, green, blue)
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FancyIndicatorContainerTabs() {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf(stringResource(R.string.send_wish), stringResource(R.string.get_wishes))

    Column {
        SecondaryTabRow(
            selectedTabIndex = state,
            indicator = { FancyAnimatedIndicator(state) },
            divider = {}
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(title) },
                    modifier = Modifier.padding(vertical = 5.dp), // Simplified padding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabIndicatorScope.FancyAnimatedIndicator(index: Int) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )
    val indicatorColor by animateColorAsState(colors[index % colors.size], label = "")
    val transition = updateTransition(index, label = "tab_indicator_transition")

    val indicatorStart by transition.animateDp(transitionSpec = { spring(dampingRatio = 1f, stiffness = 2000f) }, label = "indicator_start") {
        tabPositions[it].left
    }
    val indicatorEnd by transition.animateDp(transitionSpec = { spring(dampingRatio = 1f, stiffness = 2000f) }, label = "indicator_end") {
        tabPositions[it].right
    }


    Box(
        Modifier
            .tabIndicatorOffset(it = indicatorStart)
            .width(indicatorEnd - indicatorStart)
            .padding(1.dp)
            .fillMaxHeight()
            .drawWithContent {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = CornerRadius(100.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .background(Color.Gray)
    )
}

@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewHomeScreen() {
    Box(modifier = Modifier.padding(top = 40.dp)) {
        HomeScreen(homeKeyUiState = UiState.Loading, onGetWishKey = {})
    }
}