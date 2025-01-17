package com.vangelnum.wisher.features.home.getwish.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import com.vangelnum.wisher.features.home.User
import com.vangelnum.wisher.features.home.getwish.data.model.GetWishResponse
import kotlinx.coroutines.delay

@Composable
fun GetWishScreen(
    modifier: Modifier = Modifier,
    wishesState: UiState<List<GetWishResponse>>,
    onGetWishes: (String) -> Unit
) {
    var wishKey by remember {
        mutableStateOf("")
    }

    LaunchedEffect(wishKey) {
        if (wishKey.isNotBlank()) {
            delay(300L)
            onGetWishes(wishKey)
        }
    }

    Column(modifier = modifier) {
        WishKeyTextField(wishKey) {
            wishKey = it
        }
        when (wishesState) {
            is UiState.Loading -> {
                LoadingScreen()
            }

            is UiState.Success -> {
                if (wishesState.data.isEmpty()) {
                    Text(
                        text = "Пожеланий еще нет",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(wishesState.data) { wish ->
                            WishItem(wish = wish)
                        }
                    }
                }
            }

            is UiState.Error -> {
                ErrorScreen(errorMessage = wishesState.message)
            }

            UiState.Idle -> {}
        }
    }
}

@Composable
fun WishKeyTextField(wishKey: String, onWishKeyChange: (String) -> Unit) {
    OutlinedTextField(
        value = wishKey,
        onValueChange = {
            onWishKeyChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            ),
        placeholder = { Text("Введите ключ") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        maxLines = 1,
        shape = CircleShape
    )
}

@Composable
fun WishItem(wish: GetWishResponse) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
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
                AnimatedText(text = wish.text, modifier = Modifier.weight(1f), animationDelayPerChar = 50)
                Spacer(modifier = Modifier.width(16.dp))
                Card(
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .height(250.dp)
                        .weight(1f)
                ) {
                    AsyncImage(
                        model = wish.image,
                        contentDescription = null,
                        modifier = Modifier.height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun PreviewGetWishScreen() {
    MaterialTheme {
        WishItem(
            wish = GetWishResponse(
                id = 1,
                text = "Happy birthday! Wishing you all the best on your special day.",
                user = User(
                    id = 1,
                    name = "John Doe",
                    password = "password",
                    email = "john.doe@example.com",
                    avatarUrl = "",
                    role = "USER",
                    coins = 100
                ),
                wishDate = "2024-01-20",
                image = null,
                openDate = "2024-01-27",
                maxViewers = 5,
                isBlurred = false,
                cost = 0
            )
        )
    }
}