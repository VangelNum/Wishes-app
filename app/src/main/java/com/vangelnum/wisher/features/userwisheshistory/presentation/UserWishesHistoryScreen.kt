package com.vangelnum.wisher.features.userwisheshistory.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.home.User
import com.vangelnum.wisher.features.home.getwish.data.model.Wish

@Composable
fun UserWishesHistoryScreen(
    state: UiState<List<Wish>>,
    onNavigateToViewHistoryScreen:(wishId: Int)-> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Idle -> {}
        is UiState.Error -> {
            ErrorScreen(state.message)
        }

        is UiState.Loading -> {
            LoadingScreen("Загружаем отправленные пожелания..")
        }

        is UiState.Success -> {
            if (state.data.isEmpty()) {
                UserWishesHistoryEmptyContent()
            } else {
                UserWishesHistoryContent(state.data, onNavigateToViewHistoryScreen, modifier)
            }
        }
    }
}

@Composable
fun UserWishesHistoryEmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Нет отправленных пожеланий",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.emptystate),
            contentDescription = "Empty Sending Wishes"
        )
    }
}

@Composable
fun UserWishesHistoryContent(
    data: List<Wish>,
    onNavigateToViewHistoryScreen: (wishId: Int) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "История отправленных пожеланий",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        items(data) { wish ->
            WishHistoryCard(wish = wish, onNavigateToViewHistoryScreen)
        }
    }
}

@Composable
fun WishHistoryCard(wish: Wish, onNavigateToViewHistoryScreen: (wishId: Int) -> Unit) {
    var isFullScreenImageVisible by remember { mutableStateOf(false) }
    var textHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalContext.current.resources.displayMetrics.density

    val calculatedImageHeightDp = remember(textHeightPx) {
        max(
            300.dp,
            (textHeightPx / density).dp
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Поздравление #${wish.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Row {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = "delete"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = "edit"
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = wish.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 8.dp, end = 4.dp),
                    onTextLayout = { textLayoutResult ->
                        textHeightPx = textLayoutResult.size.height
                    },
                    textAlign = TextAlign.Center
                )
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                        .height(calculatedImageHeightDp) // Используем вычисленную высоту
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd
                    ) {
                        SubcomposeAsyncImage(
                            model = wish.image,
                            contentDescription = "Wish Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    isFullScreenImageVisible = true
                                },
                            contentScale = ContentScale.Crop,
                            loading = {
                                LoadingScreen("Загрузка изображения")
                            }
                        )
                        IconButton(onClick = {
                            isFullScreenImageVisible = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_open_in_full_24),
                                contentDescription = "open in full"
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                WishDetailItem(
                    text = if (wish.maxViewers == null || wish.maxViewers == 0) {
                        "Ограничение на просмотр не установлено"
                    } else {
                        "Ограничение на просмотры: ${wish.maxViewers}"
                    },
                    iconResId = R.drawable.baseline_remove_red_eye_24,
                    modifier = Modifier.weight(1f)
                )

                WishDetailItem(
                    text = if (wish.isBlurred) {
                        "Пожелание заблюрено"
                    } else {
                        "Пожелание не заблюрено"
                    },
                    iconResId = R.drawable.baseline_blur_on_24,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                WishDetailItem(
                    text = "Дата пожелания ${wish.wishDate}",
                    iconResId = R.drawable.baseline_calendar_month_24,
                    modifier = Modifier.weight(1f)
                )

                WishDetailItem(
                    text = "Дата открытия ${wish.openDate}",
                    iconResId = R.drawable.baseline_calendar_month_24,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedButton(
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    onNavigateToViewHistoryScreen(wish.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
            ) {
                Text("История просмотров")
            }
        }
    }

    if (isFullScreenImageVisible) {
        FullScreenImageDialog(imageUrl = wish.image) {
            isFullScreenImageVisible = false
        }
    }
}

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismissRequest)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Full Screen Wish Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .animateContentSize(animationSpec = tween(durationMillis = 500)),
                contentScale = ContentScale.Fit,
                loading = {
                    CircularProgressIndicator()
                }
            )
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_close_24),
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun WishDetailItem(text: String, iconResId: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 70.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(painter = painterResource(iconResId), contentDescription = null)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserWishesHistoryContentPreview() {
    val mockData = listOf(
        Wish(
            id = 1,
            text = "С днем рождения! Желаю всего наилучшего! Очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень очень very very",
            user = User(
                avatarUrl = null,
                email = "john.doe@example.com",
                id = 1,
                name = "John Doe",
                password = "password",
                role = "user",
                coins = 100,
                isEmailVerified = true,
                verificationCode = null
            ),
            wishDate = "2023-10-27",
            image = "https://via.placeholder.com/300/09f.png/fff&text=Image+1", // Using placeholder with text
            openDate = "2023-10-28",
            maxViewers = 10,
            isBlurred = false,
            cost = 5
        ),
        Wish(
            id = 2,
            text = "Поздравляю с новым годом!",
            user = User(
                avatarUrl = null,
                email = "jane.smith@example.com",
                id = 2,
                name = "Jane Smith",
                password = "password",
                role = "user",
                coins = 50,
                verificationCode = null,
                isEmailVerified = true
            ),
            wishDate = "2023-12-31",
            image = "https://via.placeholder.com/300/09c.png/fff&text=Image+2", // Using placeholder with text
            openDate = "2024-01-01",
            maxViewers = 5,
            isBlurred = true,
            cost = 10
        )
    )
    MaterialTheme {
        UserWishesHistoryContent(
            data = mockData,
            onNavigateToViewHistoryScreen = {  },
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserWishesHistoryEmptyContentPreview() {
    MaterialTheme {
        UserWishesHistoryEmptyContent()
    }
}