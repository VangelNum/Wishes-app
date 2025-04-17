package com.vangelnum.wishes.features.userwishviewhistory.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.features.auth.core.model.User
import com.vangelnum.wishes.features.home.getwish.data.model.Wish
import com.vangelnum.wishes.features.userwishviewhistory.data.model.ViewHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ViewHistoryScreen(
    state: UiState<List<ViewHistory>>,
    onLoadViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Idle -> {
            onLoadViewHistory()
        }

        is UiState.Error -> {
            ErrorScreen(state.message)
        }

        is UiState.Loading -> {
            LoadingScreen(stringResource(R.string.view_history_loading_message))
        }

        is UiState.Success -> {
            if (state.data.isEmpty()) {
                ViewHistoryEmpty()
            } else {
                ViewHistoryContent(state.data, modifier)
            }
        }
    }
}

@Composable
fun ViewHistoryEmpty() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.view_history_empty_message),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.emptystate),
            contentDescription = stringResource(R.string.view_history_empty_image_description)
        )
    }
}

fun formatViewTime(viewTime: String): String {
    val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val dateTime = LocalDateTime.parse(viewTime, inputFormatter)
    return dateTime.format(outputFormatter)
}


@Composable
fun ViewHistoryContent(data: List<ViewHistory>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { view ->
            ViewHistoryItem(viewHistory = view)
        }
    }
}

@Composable
fun ViewHistoryItem(viewHistory: ViewHistory) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewHistory.viewer.avatarUrl != null) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp)
                ) {
                    AsyncImage(
                        model = viewHistory.viewer.avatarUrl,
                        contentDescription = stringResource(R.string.user_icon_description),
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Card(
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.defaultprofilephoto),
                        contentDescription = stringResource(R.string.user_icon_description),
                        modifier = Modifier.size(60.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = viewHistory.viewer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = viewHistory.viewer.email,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "${formatViewTime(viewHistory.viewTime)} (UTC+0)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ViewHistoryContentPreview() {
    MaterialTheme {
        Surface {
            ViewHistoryContent(
                listOf(
                    ViewHistory(
                        id = 1,
                        viewer = User(
                            id = 55,
                            name = "chhcchccuhv",
                            password = "",
                            email = "hi301214@mail.ru",
                            avatarUrl = null,
                            role = "USER",
                            coins = 500,
                            verificationCode = null,
                            isEmailVerified = true
                        ),
                        wishOwner = User(
                            id = 1,
                            name = "admin",
                            password = "",
                            email = "vangelnum@gmail.com",
                            avatarUrl = null,
                            role = "ADMIN",
                            coins = 950,
                            verificationCode = null,
                            isEmailVerified = true
                        ),
                        wish = Wish(
                            id = 1,
                            text = "Дорогой Роберт Бернс, с днем рождения! Пусть твои стихи продолжают вдохновлять, а душа горит ярким светом творчества. Живи в сердце народа и в его песнях!",
                            user = User(
                                id = 1,
                                name = "admin",
                                password = "",
                                email = "vangelnum@gmail.com",
                                avatarUrl = null,
                                role = "ADMIN",
                                coins = 950,
                                verificationCode = null,
                                isEmailVerified = true
                            ),
                            wishDate = "2025-01-25",
                            image = "https://wishes-vangel.amvera.io/api/v1/generate/image/Enhance%20the%20following%20image%20prompt%20to%20create%20a%20more%20vivid%20and%20engaging%20greeting%20card%20design%3A%20.%20Focus%20on%20visual%20details%2C%20ensuring%20the%20composition%20is%20suitable%20for%20a%20card.%20Include%20elements%20that%20evoke%20a%20sense%20of%20celebration%20and%20joy.?model=flux&seed=457464049&width=512&height=512&nologo=true",
                            openDate = "2025-01-25",
                            maxViewers = null,
                            isBlurred = true,
                            cost = 10
                        ),
                        viewTime = "2025-01-25T21:45:28.159775"
                    )
                ),
                Modifier
            )
        }
    }
}