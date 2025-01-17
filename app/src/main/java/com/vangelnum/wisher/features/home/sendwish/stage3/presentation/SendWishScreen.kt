package com.vangelnum.wisher.features.home.sendwish.stage3.presentation

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CaretProperties
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vangelnum.wisher.R
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.presentation.ErrorScreen
import com.vangelnum.wisher.core.presentation.LoadingScreen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@Composable
fun SendWishScreen(
    modifier: Modifier = Modifier,
    holidayDate: String,
    currentDate: String,
    key: String,
    holidayName: String,
    generateImageState: UiState<String>,
    onGenerateImage: (prompt: String, model: String) -> Unit,
    modelsState: UiState<List<String>>,
    sendWishState: UiState<Unit>,
    onSendWish: (
        text: String,
        wishDate: String,
        openDate: String,
        image: String,
        maxViewers: Int?,
        isBlurred: Boolean,
        cost: Int
    ) -> Unit,
    uploadImageState: UiState<String>,
    onUploadImage: (imageUri: Uri) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onBackSendState: () -> Unit
) {
    var wishText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isBlurred by remember { mutableStateOf(false) }
    var maxViews by remember { mutableStateOf("") }
    var additionalSettingsExpanded by remember { mutableStateOf(false) }
    var openDate by remember { mutableStateOf(holidayDate) }
    var openDateLocalDate by remember { mutableStateOf(LocalDate.parse(holidayDate)) }

    val parsedHolidayDate = remember(holidayDate) { LocalDate.parse(holidayDate) }
    val parsedCurrentDate = remember(currentDate) { LocalDate.parse(currentDate) }

    val parsedDate = remember(holidayDate) {
        LocalDate.parse(holidayDate)
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM", Locale("ru")) }
    val formattedDate = parsedDate.format(dateFormatter)

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    when (sendWishState) {
        is UiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Поздравление успешно отправлено!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Скопируйте ключ и поделитесь им, чтобы другие смогли увидеть ваше поздравление.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            key,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(key))
                            Toast.makeText(context, "Ключ скопирован", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(painter = painterResource(R.drawable.baseline_content_copy_24), contentDescription = "Скопировать")
                        }
                        ShareKeyButton(key)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateToHomeScreen, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)) {
                    Text("На главную")
                }
            }
        }

        is UiState.Loading -> {
            LoadingScreen(text = "Отправляем поздравление...")
        }

        is UiState.Error -> {
            Column {
                ErrorScreen(
                    errorMessage = sendWishState.message,
                    onButtonClick = {
                        val maxViewersInt = maxViews.toIntOrNull()
                        val imageToSend = when (selectedTabIndex) {
                            0 -> if (uploadImageState is UiState.Success) uploadImageState.data else null
                            1 -> if (generateImageState is UiState.Success) generateImageState.data else null
                            else -> null
                        }
                        if (imageToSend != null) {
                            onSendWish(
                                wishText,
                                holidayDate,
                                openDate,
                                imageToSend,
                                maxViewersInt,
                                isBlurred,
                                10
                            )
                        }
                    },
                    buttonMessage = "Попробовать еще раз"
                )
                Button(
                    onClick = onBackSendState,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                ) {
                    Text("Назад")
                }
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(formattedDate, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(12.dp))
                WishText(wishText, onWishTextChange = {
                    wishText = it
                })
                Spacer(modifier = Modifier.height(12.dp))
                Text("Изображение", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(12.dp))
                TabsSelect(selectedTabIndex = selectedTabIndex, onSelectedTabIndexChange = {
                    selectedTabIndex = it
                })
                Spacer(modifier = Modifier.height(12.dp))
                val imageToSend = when (selectedTabIndex) {
                    0 -> if (uploadImageState is UiState.Success) uploadImageState.data else null
                    1 -> if (generateImageState is UiState.Success) generateImageState.data else null
                    else -> null
                }
                when (selectedTabIndex) {
                    0 -> {
                        ImageFromGallery(
                            selectedImageUri = selectedImageUri,
                            onSelectedImageUriChange = {
                                selectedImageUri = it
                            },
                            isBlurred = isBlurred,
                            uploadImageState = uploadImageState,
                            onUploadImage = onUploadImage
                        )
                    }

                    1 -> {
                        GeneratedImage(
                            generateImageState,
                            onGenerateImage,
                            wishText,
                            modelsState,
                            isBlurred = isBlurred
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                AdditionalSettings(
                    expanded = additionalSettingsExpanded,
                    onExpand = { additionalSettingsExpanded = !additionalSettingsExpanded },
                    isBlurred = isBlurred,
                    onBlurredChange = { isBlurred = it },
                    maxViews = maxViews,
                    onMaxViewsChange = {
                        if (it.isEmpty() || (it.toIntOrNull() ?: 0) <= 100000) {
                            maxViews = it
                        }
                    },
                    openDate = openDate,
                    onOpenDateChange = {
                        openDate = it
                        openDateLocalDate = LocalDate.parse(it)
                    },
                    holidayDate = parsedHolidayDate,
                    currentDate = parsedCurrentDate
                )
                Spacer(modifier = Modifier.height(12.dp))
                SendWishButton(
                    wishText,
                    imageToSend,
                    onClick = {
                        val maxViewersInt = maxViews.toIntOrNull()
                        if (imageToSend != null) {
                            onSendWish(
                                wishText,
                                holidayDate,
                                openDate,
                                imageToSend,
                                maxViewersInt,
                                isBlurred,
                                10
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ShareKeyButton(key: String) {
    val context = LocalContext.current
    IconButton(onClick = {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, key)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться ключом")
        context.startActivity(shareIntent)
    }) {
        Icon(Icons.Filled.Share, contentDescription = "Поделиться")
    }
}

@Composable
fun SendWishButton(
    wishText: String,
    imageToSend: String?,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAED581).copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
        enabled = wishText.isNotEmpty() && imageToSend != null
    ) {
        Text("Отправить", style = MaterialTheme.typography.titleLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalSettings(
    expanded: Boolean,
    onExpand: () -> Unit,
    isBlurred: Boolean,
    onBlurredChange: (Boolean) -> Unit,
    maxViews: String,
    onMaxViewsChange: (String) -> Unit,
    openDate: String,
    onOpenDateChange: (String) -> Unit,
    holidayDate: LocalDate,
    currentDate: LocalDate
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val tooltipStateMaxUsers = rememberTooltipState()
    val tooltipStateOpenDate = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onExpand)
        ) {
            Text(
                text = "Дополнительные настройки",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            val icon =
                if (expanded) R.drawable.baseline_arrow_drop_up_24 else R.drawable.baseline_arrow_drop_down_24
            Icon(painterResource(id = icon), contentDescription = null)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Заблюрить изображение")
                    Switch(checked = isBlurred, onCheckedChange = onBlurredChange)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = maxViews,
                        onValueChange = onMaxViewsChange,
                        label = {
                            Text(
                                "Максимальное количество просмотров",
                            )
                        },
                        placeholder = {
                            Text("0 - без ограничений")
                        },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        ),
                        trailingIcon = {
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip { Text("Количество людей, которые смогут открыть поздравление") }
                                },
                                state = tooltipStateMaxUsers
                            ) {
                                IconButton(onClick = {
                                    scope.launch {
                                        tooltipStateMaxUsers.show()
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_info_24),
                                        contentDescription = "info"
                                    )
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = openDate,
                        onValueChange = onOpenDateChange,
                        label = {
                            Text(
                                "Дата открытия",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                        contentDescription = "calendar"
                                    )
                                }
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip(
                                            caretProperties = CaretProperties(
                                                24.dp,
                                                24.dp
                                            )
                                        ) { Text("Дата, когда получатель сможет открыть поздравление") }
                                    },
                                    state = tooltipStateOpenDate
                                ) {
                                    IconButton(onClick = {
                                        scope.launch {
                                            tooltipStateOpenDate.show()
                                        }
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_info_24),
                                            contentDescription = "info"
                                        )
                                    }
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                    )

                }

                if (showDatePicker) {
                    val calendar = Calendar.getInstance()
                    calendar.set(
                        currentDate.year,
                        currentDate.monthValue - 1,
                        currentDate.dayOfMonth
                    )
                    val minDateMillis = calendar.timeInMillis

                    calendar.set(
                        holidayDate.year,
                        holidayDate.monthValue - 1,
                        holidayDate.dayOfMonth
                    )
                    val maxDateMillis = calendar.timeInMillis

                    val parsedOpenDate = LocalDate.parse(openDate)
                    val initialYear = parsedOpenDate.year
                    val initialMonth = parsedOpenDate.monthValue - 1
                    val initialDay = parsedOpenDate.dayOfMonth

                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                            onOpenDateChange(selectedDate.toString())
                            showDatePicker = false
                        },
                        initialYear,
                        initialMonth,
                        initialDay
                    )
                    datePickerDialog.datePicker.minDate = minDateMillis
                    datePickerDialog.datePicker.maxDate = maxDateMillis
                    datePickerDialog.setOnDismissListener { showDatePicker = false }
                    datePickerDialog.show()
                }
            }
        }
    }
}

@Composable
fun GeneratedImage(
    generateImageState: UiState<String>,
    onGenerateImage: (prompt: String, model: String) -> Unit,
    wishText: String,
    modelsState: UiState<List<String>>,
    isBlurred: Boolean
) {
    var selectedModel by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = modelsState) {
        if (modelsState is UiState.Success && modelsState.data.isNotEmpty()) {
            selectedModel = modelsState.data.first()
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        ),
        modifier = Modifier.animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            when (modelsState) {
                is UiState.Error -> {
                    ErrorScreen(errorMessage = modelsState.message)
                }

                UiState.Idle -> {}
                UiState.Loading -> {
                    LoadingScreen()
                }

                is UiState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        modelsState.data.chunked(4).forEach { rowModels ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                rowModels.forEach { modelName ->
                                    val randomColor = remember {
                                        Color(
                                            Random.nextInt(150, 220),
                                            Random.nextInt(150, 220),
                                            Random.nextInt(150, 220),
                                            255
                                        )
                                    }
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(randomColor)
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clickable {
                                                selectedModel = modelName
                                            }
                                            .then(
                                                if (selectedModel == modelName) {
                                                    Modifier.border(2.dp, Color.Green, CircleShape)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    ) {
                                        Text(
                                            modelName,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.displayLarge.copy(
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (generateImageState) {
                UiState.Idle -> {
                    Button(
                        onClick = {
                            selectedModel?.let { model ->
                                onGenerateImage(wishText, model)
                            }
                        },
                        enabled = selectedModel != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text("Генерация изображения")
                    }
                }

                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = generateImageState.message,
                        buttonMessage = "Попробовать еще раз",
                        onButtonClick = {
                            selectedModel?.let { model ->
                                onGenerateImage(wishText, model)
                            }
                        })
                }

                UiState.Loading -> {

                    Card(
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        LoadingScreen(text = "Генерируем..")
                    }
                }

                is UiState.Success -> {
                    Card(
                        shape = RoundedCornerShape(32.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        AsyncImage(
                            model = generateImageState.data,
                            contentDescription = "Сгенерированное изображение",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .then(if (isBlurred) Modifier.blur(32.dp) else Modifier),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            selectedModel?.let { model ->
                                onGenerateImage(wishText, model)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text("Попробовать еще раз")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageFromGallery(
    selectedImageUri: Uri?,
    onSelectedImageUriChange: (Uri?) -> Unit,
    isBlurred: Boolean,
    uploadImageState: UiState<String>,
    onUploadImage: (Uri) -> Unit
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onSelectedImageUriChange(uri)
            uri?.let { onUploadImage(it) }
        }
    if (selectedImageUri != null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(
                topStart = 48.dp,
                topEnd = 48.dp,
                bottomStart = 32.dp,
                bottomEnd = 32.dp
            ),
            modifier = Modifier.animateContentSize()
        ) {
            Column(modifier = Modifier
                .animateContentSize()
                .padding(8.dp)) {
                Card(shape = RoundedCornerShape(48.dp)) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Выбранное изображение",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .then(if (isBlurred) Modifier.blur(16.dp) else Modifier),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                    enabled = uploadImageState !is UiState.Loading
                ) {
                    Text(
                        "Выбрать другое изображение"
                    )
                }
                if (uploadImageState is UiState.Loading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LoadingScreen(text = "Загружаем изображение на сервер")
                } else if (uploadImageState is UiState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ErrorScreen(
                        errorMessage = uploadImageState.message,
                        buttonMessage = "Попробовать еще раз"
                    ) {
                        onUploadImage(selectedImageUri)
                    }
                }
            }
        }
    } else {
        ElevatedButton(
            onClick = {
                galleryLauncher.launch("image/*")
            }, modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            shape = RoundedCornerShape(16.dp),
            enabled = uploadImageState !is UiState.Loading
        ) {
            Text("Выбрать изображение")
        }
        if (uploadImageState is UiState.Loading) {
            LoadingScreen(text = "Подготовка к загрузке")
        } else if (uploadImageState is UiState.Error) {
            ErrorScreen(
                errorMessage = uploadImageState.message,
                buttonMessage = "Попробовать еще раз"
            ) {
                galleryLauncher.launch("image/*")
            }
        }
    }
}

@Composable
fun TabsSelect(
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit
) {
    val tabTitles = listOf("Из галереи", "Сгенерировать")
    TabRow(
        selectedTabIndex = selectedTabIndex,
        divider = {},
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)),
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onSelectedTabIndexChange(index) },
                text = { Text(title) },
                modifier = Modifier
                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
            )
        }
    }
}

@Composable
fun WishText(
    wishText: String,
    onWishTextChange: (String) -> Unit,
) {
    val maxWishTextLength = 200
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = wishText,
            onValueChange = {
                if (it.length <= maxWishTextLength) {
                    onWishTextChange(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = OutlinedTextFieldDefaults.shape
                ),
            placeholder = { Text("Введите текст поздравления") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            maxLines = 10
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "${wishText.length}/$maxWishTextLength",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
fun PreviewSendWishDataScreen() {
    Box(
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        SendWishScreen(
            holidayDate = "2024-12-31",
            key = "testkey",
            holidayName = "Chistmas",
            generateImageState = UiState.Idle,
            onGenerateImage = { _, _ -> },
            currentDate = "2023-10-27",
            modelsState = UiState.Success(
                listOf(
                    "model1",
                    "model2",
                    "model3",
                    "model4",
                    "model5",
                    "model6",
                    "model7",
                    "model8"
                )
            ),
            onSendWish = { _, _, _, _, _, _, _ ->

            },
            sendWishState = UiState.Idle,
            uploadImageState = UiState.Idle,
            onUploadImage = {},
            onNavigateToHomeScreen = {},
            onBackSendState = {}
        )
    }
}