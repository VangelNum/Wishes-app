package com.vangelnum.wisher.features.home.sendwish.createwish.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.vangelnum.wisher.core.data.UiState
import com.vangelnum.wisher.core.utils.ErrorParser
import com.vangelnum.wisher.features.home.getwish.data.model.Wish
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.GenerationImageApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.GenerationTextApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.SendWishApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.api.UploadImageApi
import com.vangelnum.wisher.features.home.sendwish.createwish.data.model.SendWishRequest
import com.vangelnum.wisher.features.home.sendwish.createwish.domain.repository.SendWishRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class SendWishRepositoryImpl @Inject constructor(
    private val generationImageApi: GenerationImageApi,
    private val generationTextApi: GenerationTextApi,
    private val sendWishApi: SendWishApi,
    private val uploadImageApi: UploadImageApi,
    @ApplicationContext private val context: Context,
    private val errorParser: ErrorParser
) : SendWishRepository {
    private val baseUrl = "https://wishesapp-vangel.amvera.io"
    private val maxSeedValue = 999999999
    override suspend fun generateImage(prompt: String, model: String): String {
        val seed = Random.nextInt(1, maxSeedValue + 1)
        val width = 512
        val height = 512
        val nologo = true
        val safe = false
        generationImageApi.generateImage(prompt, model, seed, width, height, nologo, safe)

        val uri = baseUrl.toUri().buildUpon()
            .appendPath("api")
            .appendPath("v1")
            .appendPath("generate")
            .appendPath("image")
            .appendPath(prompt)
            .appendQueryParameter("model", model)
            .appendQueryParameter("seed", seed.toString())
            .appendQueryParameter("width", width.toString())
            .appendQueryParameter("height", height.toString())
            .appendQueryParameter("nologo", nologo.toString())
            .appendQueryParameter("safe", safe.toString())
            .build()
        Log.d("tag", uri.toString())
        return uri.toString()
    }

    override suspend fun generateWishPromptByHolidayName(
        holidayName: String,
        model: String?,
        languageCode: String
    ): String {
        val improvedPrompt = "Compose a heartfelt and original wish for $holidayName, written in languageCode = $languageCode. Keep it concise, under 200 characters."
        return generationTextApi.generateText(improvedPrompt, model)
    }

    override suspend fun improveWishPrompt(
        prompt: String,
        model: String?,
        languageCode: String
    ): String {
        return try {
            val seed = Random.nextInt(0, maxSeedValue + 1)
            val improvedWishPrompt = "Improve this prompt: $prompt. It should be something new. Not just the same prompt. Your improved prompt should be written in languageCode = $languageCode. Keep it concise, under 200 characters."
            return generationTextApi.generateText(improvedWishPrompt, model, seed = seed)
        } catch (e: Exception) {
            errorParser.parseError(e)
        }
    }

    override fun getImageModels(): Flow<UiState<List<String>>> = flow {
        emit(UiState.Loading())
        try {
            val response = generationImageApi.getListOfModels()
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred ${e.message}"))
        }
    }

    override fun sendWish(
        request: SendWishRequest
    ): Flow<UiState<Wish>> = flow {
        emit(UiState.Loading())
        try {
            val response = sendWishApi.sendWish(request)
            emit(UiState.Success(response))
        } catch (e: Exception) {
            emit(UiState.Error(errorParser.parseError(e)))
        }
    }

    override fun uploadImage(imageUri: Uri): Flow<UiState<String>> = flow {
        val seed = Random.nextInt(0, maxSeedValue)
        val file = File(context.cacheDir, "temp_image_$seed.png")
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        emit(UiState.Loading())
        try {
            val response =  uploadImageApi.uploadImage(imagePart)
            emit(UiState.Success(response))
        } catch (e: HttpException) {
            emit(UiState.Error(e.message() ?: "HTTP Error"))
        } catch (e: Exception) {
            emit(UiState.Error("An unexpected error occurred ${e.message}"))
        }
    }

    override suspend fun translateTextToEnglish(prompt: String): String {

        val languageIdentifier = LanguageIdentification.getClient()
        var detectedLanguage = languageIdentifier.identifyLanguage(prompt).await()

        if (detectedLanguage == "und") {
            detectedLanguage = TranslateLanguage.RUSSIAN
        }

        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(detectedLanguage)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded().await()
            translator.translate(prompt).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getNumberWishesOfCurrentUser(): Long {
        return sendWishApi.getNumberWishesOfCurrentUser()
    }
}