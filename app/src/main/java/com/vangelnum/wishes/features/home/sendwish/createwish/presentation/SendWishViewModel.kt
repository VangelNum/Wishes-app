package com.vangelnum.wishes.features.home.sendwish.createwish.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wishes.core.data.UiState
import com.vangelnum.wishes.core.data.UiState.Idle
import com.vangelnum.wishes.features.home.sendwish.createwish.data.model.SendWishRequest
import com.vangelnum.wishes.features.home.sendwish.createwish.domain.repository.SendWishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendWishViewModel @Inject constructor(
    private val sendWishRepository: SendWishRepository
) : ViewModel() {

    private val _sendWishUiState = MutableStateFlow(SendWishUiState())
    val sendWishUiState = _sendWishUiState.asStateFlow()

    init {
        onEvent(SendWishEvent.OnGetImageModels)
        onEvent(SendWishEvent.OnGetNumberOfWishes)
    }

    fun onEvent(event: SendWishEvent) {
        when (event) {
            is SendWishEvent.OnGenerateImage -> {
                generateImageWithPrompt(event.prompt, event.model)
            }

            SendWishEvent.OnGetImageModels -> {
                getImageModels()
            }

            is SendWishEvent.OnSendWish -> {
                sendWish(
                    event.text,
                    event.wishDate,
                    event.openDate,
                    event.image,
                    event.maxViewers,
                    event.isBlurred,
                    event.cost
                )
            }

            is SendWishEvent.OnUploadImage -> {
                uploadImage(event.imageUri)
            }

            SendWishEvent.OnSendBackState -> {
                _sendWishUiState.update { it.copy(sendWishState = Idle()) }
            }

            is SendWishEvent.OnImproveWishPrompt -> {
                improveWishPrompt(event.prompt, event.model, event.languageCode)
            }

            is SendWishEvent.OnGenerateWishPromptByHoliday -> {
                generateWishPromptByHolidayName(event.holiday, event.model, event.languageCode)
            }

            SendWishEvent.OnGetNumberOfWishes -> getNumberWishesOfCurrentUser()
        }
    }

    private fun getNumberWishesOfCurrentUser() {
        viewModelScope.launch {
            _sendWishUiState.update {
                it.copy(numberOfWishes = sendWishRepository.getNumberWishesOfCurrentUser())
            }
        }
    }

    private fun generateWishPromptByHolidayName(
        holidayName: String,
        model: String?,
        languageCode: String
    ) {
        _sendWishUiState.update { it.copy(generateTextState = UiState.Loading()) }
        viewModelScope.launch {
            try {
                val translatedText = sendWishRepository.translateTextToEnglish(holidayName)
                val response = sendWishRepository.generateWishPromptByHolidayName(
                    translatedText,
                    model,
                    languageCode
                )
                _sendWishUiState.update {
                    it.copy(
                        generateTextState = UiState.Success(
                            response
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _sendWishUiState.update { it.copy(generateTextState = UiState.Error(e.localizedMessage)) }
            }
        }
    }

    private fun improveWishPrompt(prompt: String, model: String? = null, languageCode: String) {
        _sendWishUiState.update { it.copy(generateTextState = UiState.Loading()) }
        viewModelScope.launch {
            try {
                val translatedText = sendWishRepository.translateTextToEnglish(prompt)
                val response = sendWishRepository.improveWishPrompt(translatedText, model, languageCode)
                _sendWishUiState.update { it.copy(generateTextState = UiState.Success(response)) }
            } catch (e: Exception) {
                e.printStackTrace()
                _sendWishUiState.update { it.copy(generateTextState = UiState.Error(e.localizedMessage)) }
            }
        }
    }

    private fun sendWish(
        text: String,
        wishDate: String,
        openDate: String,
        image: String,
        maxViewers: Int?,
        blurred: Boolean,
        cost: Int
    ) {
        viewModelScope.launch {
            sendWishRepository.sendWish(SendWishRequest(text, wishDate, openDate, image, maxViewers, blurred, cost)).collect { state->
                _sendWishUiState.update {
                    it.copy(sendWishState = state)
                }
            }
        }
    }

    private fun getImageModels() {
        _sendWishUiState.update { it.copy(modelsListState = UiState.Loading()) }
        viewModelScope.launch {
            sendWishRepository.getImageModels().collectLatest { state ->
                _sendWishUiState.update {
                    it.copy(
                        modelsListState = state
                    )
                }
            }
        }
    }

    fun generateImageWithPrompt(prompt: String, model: String) {
        _sendWishUiState.update { it.copy(generateImageState = UiState.Loading()) }
        viewModelScope.launch {
            try {
                val translateText = sendWishRepository.translateTextToEnglish(prompt)
                val improvedPrompt = "Enhance the following image prompt to create a more vivid and engaging greeting card design: $translateText. Focus on visual details, ensuring the composition is suitable for a card. Include elements that evoke a sense of celebration and joy."
                val response = sendWishRepository.generateImage(improvedPrompt, model)
                _sendWishUiState.update { it.copy(generateImageState = UiState.Success(response)) }
            } catch (e: Exception) {
                e.printStackTrace()
                _sendWishUiState.update { it.copy(generateImageState = UiState.Error("${e.message}")) }
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        _sendWishUiState.update { it.copy(uploadImageState = UiState.Loading()) }
        viewModelScope.launch {
            sendWishRepository.uploadImage(imageUri).collectLatest { state ->
                _sendWishUiState.update {
                    it.copy(
                        uploadImageState = state
                    )
                }
            }
        }
    }
}