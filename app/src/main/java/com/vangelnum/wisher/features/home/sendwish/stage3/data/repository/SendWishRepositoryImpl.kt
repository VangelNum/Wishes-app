package com.vangelnum.wisher.features.home.sendwish.stage3.data.repository

import android.content.Context
import android.net.Uri
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.GenerateImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.SendWishApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.api.UploadImageApi
import com.vangelnum.wisher.features.home.sendwish.stage3.data.model.SendWishRequest
import com.vangelnum.wisher.features.home.sendwish.stage3.domain.repository.SendWishRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

class SendWishRepositoryImpl @Inject constructor(
    private val generateImageApi: GenerateImageApi,
    private val sendWishApi: SendWishApi,
    private val uploadImageApi: UploadImageApi,
    @ApplicationContext private val context: Context
) : SendWishRepository {
    private val baseUrl = "https://image.pollinations.ai"
    private val imgbbApiKey = "f90248ad8f4b1e262a5e8e7603645cc1"
    private val maxSeedValue = 999999999

    override suspend fun generateImage(prompt: String, model: String): String {
        val seed = Random.nextInt(1, maxSeedValue + 1)
        val width = 512
        val height = 512
        val nologo = true
        generateImageApi.generateImage(prompt, model, seed, width, height, nologo)

        val uri = Uri.parse(baseUrl).buildUpon()
            .appendPath("prompt")
            .appendPath(prompt)
            .appendQueryParameter("model", model)
            .appendQueryParameter("seed", seed.toString())
            .appendQueryParameter("width", width.toString())
            .appendQueryParameter("height", height.toString())
            .appendQueryParameter("nologo", nologo.toString())
            .build()

        return uri.toString()
    }

    override suspend fun listOfModels(): List<String> {
        return generateImageApi.getListOfModels()
    }

    override suspend fun sendWish(
        request: SendWishRequest
    ) {
        sendWishApi.sendWish(request)
    }

    override suspend fun uploadImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, "temp_image.png")
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val response = uploadImageApi.uploadImage(imgbbApiKey, imagePart)

        return@withContext response.data.url
    }
}