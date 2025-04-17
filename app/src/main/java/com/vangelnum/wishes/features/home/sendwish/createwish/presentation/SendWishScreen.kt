package com.vangelnum.wishes.features.home.sendwish.createwish.presentation

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.vangelnum.wishes.R
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.presentation.ErrorScreen
import com.vangelnum.wishes.core.presentation.LoadingScreen
import com.vangelnum.wishes.core.presentation.SnackbarController
import com.vangelnum.wishes.core.presentation.SnackbarEvent
import com.vangelnum.wishes.core.utils.string
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@Composable
fun SendWishScreen(
    modifier: Modifier = Modifier,
    holidayDate: String,
    currentDate: String,
    key: String,
    languageCode: String,
    sendWishUiState: SendWishUiState,
    onNavigateToHomeScreen: () -> Unit,
    onEvent: (SendWishEvent) -> Unit,
    refreshUserInfo: () -> Unit,
    userCoins: Int
) {
    LaunchedEffect(sendWishUiState.sendWishState) {
        if (sendWishUiState.sendWishState is UiState.Success) {
            refreshUserInfo()
        }
    }

    val parsedHolidayDate = remember(holidayDate) {
        LocalDate.parse(holidayDate)
    }
    val parsedCurrentDate = remember(currentDate) {
        LocalDate.parse(currentDate)
    }

    val formattedHolidayDate by remember(parsedHolidayDate, languageCode) {
        derivedStateOf {
            val locale = Locale(languageCode)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM", locale)
            parsedHolidayDate.format(formatter)
        }
    }

    when (val state = sendWishUiState.sendWishState) {
        is UiState.Success -> {
            SuccessSendWishContent(
                modifier = modifier,
                key = key,
                wishImage = state.data.image,
                wishText = state.data.text,
                onNavigateToHomeScreen = onNavigateToHomeScreen
            )
        }
        is UiState.Loading -> {
            LoadingScreen(loadingText = stringResource(R.string.sending_wish_loading))
        }

        is UiState.Error -> {
            SendWishErrorContent(
                errorMessage = state.message,
                onCancel = { onEvent(SendWishEvent.OnSendBackState) }
            )
        }

        else -> {
            SendWishInputContent(
                modifier = modifier,
                formattedHolidayDate = formattedHolidayDate,
                holidayDate = holidayDate,
                parsedHolidayDate = parsedHolidayDate,
                parsedCurrentDate = parsedCurrentDate,
                languageCode = languageCode,
                sendWishUiState = sendWishUiState,
                onEvent = onEvent,
                userCoins = userCoins
            )
        }
    }
}

@Composable
private fun SendWishErrorContent(
    errorMessage: String,
    onCancel: () -> Unit
) {
    ErrorScreen(
        errorMessage = errorMessage,
        buttonMessage = stringResource(R.string.back_button),
        onButtonClick = onCancel
    )
}

@Composable
private fun SendWishInputContent(
    modifier: Modifier = Modifier,
    formattedHolidayDate: String,
    holidayDate: String,
    parsedHolidayDate: LocalDate,
    parsedCurrentDate: LocalDate,
    languageCode: String,
    sendWishUiState: SendWishUiState,
    onEvent: (SendWishEvent) -> Unit,
    userCoins: Int
) {
    var wishText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isBlurred by remember { mutableStateOf(false) }
    var maxViews by remember { mutableStateOf("") }
    var additionalSettingsExpanded by remember { mutableStateOf(false) }
    var openDate by remember(holidayDate) { mutableStateOf(holidayDate) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(sendWishUiState.generateTextState) {
        val state = sendWishUiState.generateTextState
        if (state is UiState.Success) {
            wishText = state.data
        }
    }

    val imageToSend: String? by remember(
        selectedTabIndex,
        sendWishUiState.uploadImageState,
        sendWishUiState.generateImageState
    ) {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> (sendWishUiState.uploadImageState as? UiState.Success)?.data
                1 -> (sendWishUiState.generateImageState as? UiState.Success)?.data
                else -> null
            }
        }
    }

    val isSendButtonEnabled by remember(wishText, imageToSend, sendWishUiState) {
        derivedStateOf {
            wishText.isNotBlank() && imageToSend != null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Text(formattedHolidayDate, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        WishTextContent(
            wishText = wishText,
            onWishTextChange = { wishText = it },
            generateTextState = sendWishUiState.generateTextState,
            onGenerateText = {
                onEvent(
                    SendWishEvent.OnImproveWishPrompt(
                        wishText,
                        languageCode = languageCode
                    )
                )
            },
            sendWishUiState = sendWishUiState
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            stringResource(R.string.image_section_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        TabsSelect(
            selectedTabIndex = selectedTabIndex,
            onSelectedTabIndexChange = { selectedTabIndex = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> ImageFromGallery(
                selectedImageUri = selectedImageUri,
                onSelectedImageUriChange = { uri ->
                    selectedImageUri = uri
                    uri?.let { onEvent(SendWishEvent.OnUploadImage(it)) }
                },
                isBlurred = isBlurred,
                uploadImageState = sendWishUiState.uploadImageState,
                onRetryUpload = { uri ->
                    uri?.let { onEvent(SendWishEvent.OnUploadImage(it)) }
                }
            )

            1 -> GeneratedImage(
                generateImageState = sendWishUiState.generateImageState,
                onGenerateImage = { model ->
                    onEvent(SendWishEvent.OnGenerateImage(wishText, model))
                },
                wishText = wishText,
                modelsState = sendWishUiState.modelsListState,
                isBlurred = isBlurred
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.weight(1f))

        AdditionalSettings(
            expanded = additionalSettingsExpanded,
            onExpand = { additionalSettingsExpanded = !additionalSettingsExpanded },
            isBlurred = isBlurred,
            onBlurredChange = { isBlurred = it },
            maxViews = maxViews,
            onMaxViewsChange = { newValue ->
                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && (newValue.toLongOrNull()
                        ?: 0) <= 100000)) {
                    maxViews = newValue
                }
            },
            openDate = openDate,
            onOpenDateChange = { openDate = it },
            holidayDate = parsedHolidayDate,
            currentDate = parsedCurrentDate
        )
        Spacer(modifier = Modifier.height(12.dp))

        SendWishButton(
            enabled = isSendButtonEnabled,
            onClick = {
                val cost = if (sendWishUiState.numberOfWishes != null && sendWishUiState.numberOfWishes < 5) {
                    0
                } else {
                    10
                }

                if (cost > 0 && userCoins < 10) {
                    scope.launch {
                        SnackbarController.sendEvent(SnackbarEvent(string(context, R.string.not_enough_coins)))
                    }
                } else {
                    imageToSend?.let { imageUrl ->
                        val maxViewersInt = maxViews.toIntOrNull()
                        onEvent(
                            SendWishEvent.OnSendWish(
                                wishText,
                                holidayDate,
                                openDate,
                                imageToSend!!,
                                maxViewersInt,
                                isBlurred,
                                cost
                            )
                        )
                    }
                }
            },
            sendWishUiState.numberOfWishes
        )
    }
}

@Composable
private fun SendWishButton(
    enabled: Boolean,
    onClick: () -> Unit,
    numberOfWishes: Long?
) {
    Button(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAED581).copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
        enabled = enabled
    ) {
        Text(stringResource(R.string.send_button), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.width(8.dp))
        AnimatedVisibility(numberOfWishes != null) {
            if (numberOfWishes != null) {
                if (numberOfWishes >= 5) {
                    Card {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(10.toString())
                            Image(painterResource(R.drawable.coin), modifier = Modifier.size(16.dp), contentDescription = null)
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFB5FCCD))
                    ) {
                        Text("Free", modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdditionalSettings(
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
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onExpand)
                .padding(bottom = if (expanded) 8.dp else 0.dp)
        ) {
            Text(
                text = stringResource(R.string.additional_settings_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            val iconRes =
                if (expanded) R.drawable.baseline_arrow_drop_up_24 else R.drawable.baseline_arrow_drop_down_24
            Icon(
                painterResource(id = iconRes),
                contentDescription = stringResource(if (expanded) R.string.collapse_settings else R.string.expand_settings)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column {
                SettingRow(label = stringResource(R.string.blur_image_setting)) {
                    Switch(checked = isBlurred, onCheckedChange = onBlurredChange)
                }
                Spacer(modifier = Modifier.height(8.dp))
                MaxViewsSetting(
                    maxViews = maxViews,
                    onMaxViewsChange = onMaxViewsChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                OpenDateSetting(
                    openDate = openDate,
                    onClick = { showDatePicker = true }
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialogComposable(
            context = LocalContext.current,
            initialDate = LocalDate.parse(openDate),
            minDate = currentDate,
            maxDate = holidayDate,
            onDateSelected = { selectedDate ->
                onOpenDateChange(selectedDate.toString())
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun SettingRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaxViewsSetting(
    maxViews: String,
    onMaxViewsChange: (String) -> Unit
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = maxViews,
            onValueChange = onMaxViewsChange,
            label = { Text(stringResource(R.string.max_views_label)) },
            placeholder = { Text(stringResource(R.string.max_views_placeholder)) },
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
                    tooltip = { PlainTooltip { Text(stringResource(R.string.max_views_tooltip)) } },
                    state = tooltipState
                ) {
                    IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_info_24),
                            contentDescription = stringResource(R.string.info_icon_description)
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpenDateSetting(
    openDate: String,
    onClick: () -> Unit
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    val displayDate by remember(openDate) {
        derivedStateOf {
            LocalDate.parse(openDate)?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                ?: openDate
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = displayDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.open_date_label)) },
            modifier = Modifier
                .weight(1f),
            trailingIcon = {
                Row {
                    IconButton(onClick = onClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = stringResource(R.string.calendar_icon_description)
                        )
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.open_date_tooltip))
                            }
                        },
                        state = tooltipState
                    ) {
                        IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_info_24),
                                contentDescription = stringResource(R.string.info_icon_description)
                            )
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            interactionSource = remember { MutableInteractionSource() },
        )
    }
}


@Composable
private fun DatePickerDialogComposable(
    context: android.content.Context,
    initialDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance()

    calendar.timeInMillis = 0
    calendar.set(minDate.year, minDate.monthValue - 1, minDate.dayOfMonth)
    val minDateMillis = calendar.timeInMillis

    calendar.timeInMillis = 0
    calendar.set(maxDate.year, maxDate.monthValue - 1, maxDate.dayOfMonth)
    val maxDateMillis = calendar.timeInMillis

    val datePickerDialog = remember(context, initialDate, minDateMillis, maxDateMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).apply {
            datePicker.minDate = minDateMillis
            datePicker.maxDate = maxDateMillis
            setOnDismissListener { onDismiss() }
        }
    }

    LaunchedEffect(Unit) {
        datePickerDialog.show()
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GeneratedImage(
    generateImageState: UiState<String>,
    onGenerateImage: (model: String) -> Unit,
    wishText: String,
    modelsState: UiState<List<String>>,
    isBlurred: Boolean
) {
    var selectedModel by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(modelsState) {
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
        modifier = Modifier.animateContentSize(animationSpec = tween(300))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            when (modelsState) {
                is UiState.Loading -> LoadingScreen(loadingText = stringResource(R.string.loading_models))
                is UiState.Error -> ErrorScreen(
                    errorMessage = modelsState.message,
                )

                is UiState.Success -> {
                    if (modelsState.data.isNotEmpty()) {
                        FlowRow(
                            maxItemsInEachRow = 4,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            modelsState.data.forEach { modelName ->
                                ModelItem(
                                    modelName = modelName,
                                    isSelected = selectedModel == modelName,
                                    onClick = { selectedModel = modelName }
                                )
                            }
                        }
                    } else {
                        Text(
                            stringResource(R.string.no_models_available),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is UiState.Idle -> Unit
            }

            Spacer(modifier = Modifier.height(8.dp))

            val canGenerate = selectedModel != null && wishText.isNotBlank()

            when (generateImageState) {
                is UiState.Idle -> {
                    Button(
                        onClick = { selectedModel?.let { onGenerateImage(it) } },
                        enabled = canGenerate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text(stringResource(R.string.generate_image_button))
                    }
                }

                is UiState.Loading -> {
                    ImageLoadingPlaceholder(loadingText = stringResource(R.string.generating_image_loading))
                }

                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = generateImageState.message,
                        buttonMessage = stringResource(R.string.retry_button),
                        onButtonClick = { selectedModel?.let { onGenerateImage(it) } },
                        modifier = Modifier.aspectRatio(1f),
                        buttonShape = RoundedCornerShape(16.dp),
                        customPadding = 0.dp
                    )
                }

                is UiState.Success -> {
                    GeneratedImageView(
                        imageUrl = generateImageState.data,
                        isBlurred = isBlurred
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { selectedModel?.let { onGenerateImage(it) } },
                        enabled = canGenerate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text(stringResource(R.string.generate_again_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelItem(
    modelName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val randomColor = remember(modelName) {
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
            .size(80.dp)
            .clip(CircleShape)
            .background(randomColor)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .padding(4.dp)
    ) {
        Text(
            text = modelName,
            color = Color.Black,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun ImageLoadingPlaceholder(loadingText: String) {
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
        LoadingScreen(loadingText = loadingText)
    }
}

@Composable
private fun GeneratedImageView(imageUrl: String, isBlurred: Boolean) {
    Card(
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.animateContentSize()
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.generated_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .then(if (isBlurred) Modifier.blur(32.dp) else Modifier),
            contentScale = ContentScale.Crop,
            loading = {
                ImageLoadingPlaceholder(loadingText = stringResource(R.string.loading_generated_image))
            },
            error = {
                ErrorScreen(
                    errorMessage = stringResource(R.string.error_loading_image),
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        )
    }
}


@Composable
private fun ImageFromGallery(
    selectedImageUri: Uri?,
    onSelectedImageUriChange: (Uri?) -> Unit,
    isBlurred: Boolean,
    uploadImageState: UiState<String>,
    onRetryUpload: (Uri?) -> Unit
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onSelectedImageUriChange(uri)
        }

    val isLoading = uploadImageState is UiState.Loading
    val hasError = uploadImageState is UiState.Error

    Column(modifier = Modifier.animateContentSize()) {
        if (selectedImageUri != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(
                    topStart = 48.dp,
                    topEnd = 48.dp,
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                ),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Card(shape = RoundedCornerShape(32.dp)) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(R.string.selected_image_description),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .then(if (isBlurred) Modifier.blur(16.dp) else Modifier),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                        enabled = !isLoading
                    ) {
                        Text(stringResource(R.string.select_other_image_button))
                    }
                }
            }
            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LoadingScreen(loadingText = stringResource(R.string.uploading_image_loading))
            } else if (hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorScreen(
                    errorMessage = uploadImageState.message,
                    buttonMessage = stringResource(R.string.retry_button),
                    onButtonClick = { onRetryUpload(selectedImageUri) }
                )
            }
        } else {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.select_image_button))
            }
            if (hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorScreen(
                    errorMessage = uploadImageState.message,
                )
            }
            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LoadingScreen(loadingText = stringResource(R.string.preparing_upload_loading))
            }
        }
    }
}

@Composable
private fun TabsSelect(
    selectedTabIndex: Int,
    onSelectedTabIndexChange: (Int) -> Unit
) {
    val tabTitles =
        listOf(stringResource(R.string.tab_gallery), stringResource(R.string.tab_generate))
    TabRow(
        selectedTabIndex = selectedTabIndex,
        divider = {},
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onSelectedTabIndexChange(index) },
                text = { Text(title, style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun WishTextContent(
    wishText: String,
    onWishTextChange: (String) -> Unit,
    generateTextState: UiState<String>,
    onGenerateText: () -> Unit,
    sendWishUiState: SendWishUiState
) {
    val isLoading = generateTextState is UiState.Loading
    val hasError = generateTextState is UiState.Error

    Column(modifier = Modifier.animateContentSize()) {
        if (isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
            ) {
                LoadingScreen(
                    loadingText = stringResource(R.string.generating_text_loading),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = OutlinedTextFieldDefaults.shape
                    )
            ) {
                OutlinedTextField(
                    value = wishText,
                    onValueChange = {
                        if (it.length <= 250) {
                            onWishTextChange(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 120.dp)
                        .padding(end = 50.dp),
                    placeholder = { Text(stringResource(R.string.wish_text_placeholder)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 10
                )

                Box(
                    modifier = Modifier.matchParentSize()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        AnimatedVisibility(
                            visible = wishText.isNotEmpty(),
                            enter = slideInHorizontally { it / 2 } + expandVertically(expandFrom = Alignment.Top),
                            exit = slideOutHorizontally { it / 2 } + shrinkVertically(shrinkTowards = Alignment.Top)
                        ) {
                            IconButton(
                                onClick = { onWishTextChange("") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_close_24),
                                    contentDescription = stringResource(R.string.clear_text_button),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${wishText.length}/250",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    )
                }
            }

            if (hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorScreen(
                    errorMessage = generateTextState.message,
                    buttonMessage = stringResource(R.string.retry_button),
                    onButtonClick = onGenerateText,
                    customPadding = 0.dp,
                    buttonShape = RoundedCornerShape(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onGenerateText,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
            enabled = wishText.isNotBlank() && sendWishUiState.generateTextState !is UiState.Loading
        ) {
            Text(stringResource(R.string.regenerate_text_button))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Composable
private fun PreviewSendWishScreenIdle() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            SendWishScreen(
                holidayDate = "2024-12-31",
                key = "previewKey",
                sendWishUiState = SendWishUiState(
                    modelsListState = UiState.Success(
                        listOf(
                            "Cartoon",
                            "Realistic",
                            "Anime",
                            "Pixel Art",
                            "Fantasy"
                        )
                    ),
                    uploadImageState = UiState.Idle(),
                    generateImageState = UiState.Idle(),
                    generateTextState = UiState.Idle(),
                    sendWishState = UiState.Idle()
                ),
                currentDate = "2024-10-27",
                onNavigateToHomeScreen = {},
                onEvent = {},
                languageCode = "ru",
                refreshUserInfo = {},
                userCoins = 0
            )
        }
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
private fun PreviewSendWishScreenLoading() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            SendWishScreen(
                holidayDate = "2024-07-04",
                key = "previewKey",
                sendWishUiState = SendWishUiState(sendWishState = UiState.Loading()),
                currentDate = "2024-07-01",
                onNavigateToHomeScreen = {},
                onEvent = {},
                languageCode = "en",
                refreshUserInfo = {},
                userCoins = 0
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSendWishScreenError() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            SendWishScreen(
                holidayDate = "2024-05-01",
                key = "previewKey",
                sendWishUiState = SendWishUiState(sendWishState = UiState.Error("Network connection failed. Please try again.")),
                currentDate = "2024-04-30",
                onNavigateToHomeScreen = {},
                onEvent = {},
                languageCode = "ru",
                refreshUserInfo = {},
                userCoins = 0
            )
        }
    }
}