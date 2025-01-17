package com.vangelnum.wisher.features.home.sendwish.stage3.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.SendWishRequest
import com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository.SendWishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendWishViewModel @Inject constructor(
    private val sendWishRepository: SendWishRepository
) : ViewModel() {
    private val _modelsList = MutableStateFlow<UiState<List<String>>>(UiState.Idle)
    val modelsList = _modelsList.asStateFlow()

    private val _generateImageState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val generateImageState = _generateImageState.asStateFlow()

    private val _sendWishState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val sendWishState = _sendWishState.asStateFlow()

    private val _uploadImageState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadImageState = _uploadImageState.asStateFlow()

    init {
        onEvent(SendWishEvent.OnGetModels)
    }

    fun onEvent(event: SendWishEvent) {
        when (event) {
            is SendWishEvent.OnGenerateImage -> {
                generateImage(event.prompt, event.model)
            }

            SendWishEvent.OnGetModels -> {
                getModels()
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
                _sendWishState.value = UiState.Idle
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
        _sendWishState.value = UiState.Loading
        viewModelScope.launch {
            try {
                sendWishRepository.sendWish(
                    SendWishRequest(
                        text,
                        wishDate,
                        openDate,
                        image,
                        maxViewers,
                        blurred,
                        cost
                    )
                )
                _sendWishState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                _sendWishState.value = UiState.Error(e.message ?: "Failed to send wish")
            }
        }
    }

    private fun getModels() {
        _modelsList.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = sendWishRepository.listOfModels()
                _modelsList.value = UiState.Success(response)
            } catch (e: Exception) {
                e.printStackTrace()
                _modelsList.value = UiState.Error("Failed to get models: ${e.message}")
            }
        }
    }

    fun generateImage(prompt: String, model: String) {
        _generateImageState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = sendWishRepository.generateImage(prompt, model)
                _generateImageState.value = UiState.Success(response)
            } catch (e: Exception) {
                e.printStackTrace()
                _generateImageState.value = UiState.Error("Failed to generate image: ${e.message}")
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        _uploadImageState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val imageUrl = sendWishRepository.uploadImage(imageUri)
                _uploadImageState.value = UiState.Success(imageUrl)
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadImageState.value = UiState.Error("Failed to upload image: ${e.message}")
            }
        }
    }
}