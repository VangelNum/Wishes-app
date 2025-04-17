package com.vangelnum.wishes.features.keylogshistory.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.features.auth.core.model.User
import com.vangelnum.wishes.features.auth.core.utils.copyKey
import com.vangelnum.wishes.features.keylogshistory.data.model.KeyLogsHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun KeyLogsHistoryScreen(
    state: UiState<List<KeyLogsHistory>>,
    onSearchByKey: (key: String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is UiState.Error -> {
            ErrorScreen(stringResource(R.string.key_logs_history_error_message))
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(stringResource(R.string.key_logs_history_loading_message))
        }

        is UiState.Success -> {
            if (state.data.isEmpty()) {
                KeyLogsHistoryEmpty()
            } else {
                KeyLogsHistoryContent(state.data, onSearchByKey, modifier)
            }
        }
    }
}

@Composable
fun KeyLogsHistoryEmpty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            stringResource(R.string.key_logs_history_empty_list),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(R.drawable.emptystate),
            contentDescription = stringResource(R.string.key_logs_history_empty_image_description)
        )
    }
}

@Composable
fun KeyLogsHistoryContent(
    data: List<KeyLogsHistory>,
    onSearchByKey: (key: String) -> Unit,
    modifier: Modifier
) {
    val showDuplicatesState = remember { mutableStateOf(false) }
    val sortedData = data.sortedByDescending { it.id }
    val filteredData = if (showDuplicatesState.value) {
        sortedData
    } else {
        sortedData.distinctBy { it.key }
    }


    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.key_logs_history_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(8.dp).padding(start = 8.dp, end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.key_logs_history_show_duplicates))
                Switch(
                    checked = showDuplicatesState.value,
                    onCheckedChange = { showDuplicatesState.value = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredData, key = { it.id }) { keyLogsInfo ->
                KeyLogsHistoryCard(keyLogsInfo, onSearchByKey, Modifier.animateItem())
            }
        }
    }
}


@Composable
fun KeyLogsHistoryCard(
    keyLogsInfo: KeyLogsHistory,
    onSearchByKey: (key: String) -> Unit,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = keyLogsInfo.key,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val viewedAtDateTime =
                    LocalDateTime.parse(keyLogsInfo.viewedAt, DateTimeFormatter.ISO_DATE_TIME)
                val formattedDate =
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(viewedAtDateTime)

                Text(
                    text = stringResource(R.string.key_logs_history_viewed_at, formattedDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = { copyKey(clipboardManager, keyLogsInfo.key, scope, context) }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_content_copy_24),
                    contentDescription = stringResource(R.string.copy_key_description)
                )
            }
            IconButton(onClick = { onSearchByKey(keyLogsInfo.key) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.key_logs_history_search_icon_description)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "KeyLogsHistoryScreen Loading")
@Composable
fun KeyLogsHistoryScreenLoadingPreview() {
    KeyLogsHistoryScreen(state = UiState.Loading(), onSearchByKey = {})
}

@Preview(showBackground = true, name = "KeyLogsHistoryScreen Error")
@Composable
fun KeyLogsHistoryScreenErrorPreview() {
    KeyLogsHistoryScreen(state = UiState.Error(message = "Some error"), onSearchByKey = {})
}

@Preview(showBackground = true, name = "KeyLogsHistoryScreen Empty")
@Composable
fun KeyLogsHistoryScreenEmptyPreview() {
    KeyLogsHistoryScreen(state = UiState.Success(emptyList()), onSearchByKey = {})
}

@Preview(showBackground = true, name = "KeyLogsHistoryScreen Content")
@Composable
fun KeyLogsHistoryScreenContentPreview() {
    val sampleData = listOf(
        KeyLogsHistory(
            id = 1,
            key = "ABC-123-DEF-456",
            viewedAt = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_DATE_TIME),
            viewer = User(
                id = 101,
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password",
                role = "user",
                coins = 100,
                verificationCode = null,
                isEmailVerified = true,
                avatarUrl = null
            )
        ),
        KeyLogsHistory(
            id = 2,
            key = "GHI-789-JKL-012",
            viewedAt = LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_DATE_TIME),
            viewer = User(
                id = 102,
                name = "Jane Smith",
                email = "jane.smith@example.com",
                password = "password",
                role = "user",
                coins = 150,
                verificationCode = null,
                isEmailVerified = true,
                avatarUrl = null
            )
        ),
        KeyLogsHistory( // Duplicate key entry
            id = 3,
            key = "ABC-123-DEF-456",
            viewedAt = LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ISO_DATE_TIME),
            viewer = User(
                id = 101,
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password",
                role = "user",
                coins = 100,
                verificationCode = null,
                isEmailVerified = true,
                avatarUrl = null
            )
        )
    )
    KeyLogsHistoryScreen(state = UiState.Success(sampleData), onSearchByKey = {}, Modifier)
}