package com.vangelnum.wisher.features.home.getwish.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.core.presentation.SmallLoadingIndicator
import com.vangelnum.wisher.features.home.User
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import kotlinx.coroutines.delay

@Composable
fun GetWishScreenToBottomSheet(
    state: UiState<Wish>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        when (state) {
            is UiState.Error -> {
                ErrorScreen(errorMessage = state.message)
            }

            is UiState.Idle -> {}

            is UiState.Loading -> {
                LoadingScreen(loadingText = stringResource(R.string.loading_wish))
            }

            is UiState.Success -> {
                WishContent(state.data)
            }
        }
    }
}

@Composable
fun WishContent(wish: Wish) {

    Column(modifier = Modifier.padding(16.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (wish.user.avatarUrl == null) {
                    Image(
                        painter = painterResource(R.drawable.defaultprofilephoto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = wish.user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = wish.user.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedText(
                text = wish.text,
                modifier = Modifier.weight(1f),
                animationDelayPerChar = 50
            )
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                SubcomposeAsyncImage(
                    model = wish.image,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(),
                    loading = {
                        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            SmallLoadingIndicator()
                        }
                    },
                    error = {
                        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            ErrorScreen(stringResource(R.string.error_loading_image))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    animationDelayPerChar: Long
) {
    val displayedText = remember { mutableStateOf("") }

    LaunchedEffect(text) {
        displayedText.value = ""
        text.forEach { char ->
            displayedText.value += char
            delay(animationDelayPerChar)
        }
    }

    Text(
        text = displayedText.value,
        style = style,
        modifier = modifier
    )
}

@Preview
@Composable
fun GetWishScreenToBottomSheetPreview() {
    GetWishScreenToBottomSheet(
        state = UiState.Success(
            Wish(
                id = 1,
                user = User(
                    id = 1,
                    name = "John Doe",
                    avatarUrl = "https://example.com/avatar.jpg",
                    email = "vangel@mail.ru",
                    role = "",
                    coins = 100,
                    password = "",
                    isEmailVerified = true,
                    verificationCode = null
                ),
                text = "Happy birthday",
                image = "",
                openDate = "2023-01-01T00:00:00Z",
                wishDate = "2023-01-01T00:00:00Z",
                maxViewers = 10,
                cost = 50,
                isBlurred = false
            )
        )
    )
}