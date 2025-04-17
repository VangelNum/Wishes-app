package com.vangelnum.wishes.features.auth.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.vangelnum.wishes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.util.UUID

fun shareKey(key: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, key)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Key")
    context.startActivity(shareIntent)
}


fun shareWish(key: String, wishText: String, wishImage: String, context: Context, scope: CoroutineScope) {
    scope.launch {
        val imageUri: Uri? = try {
            val downloadedFile = downloadImage(context, wishImage)
            if (downloadedFile == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.share_image_download_failed), Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            getFileProviderUri(context, downloadedFile)

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.share_image_preparation_error, e.message),
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@launch
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            val appInfoText = context.getString(R.string.app_info_text, key)
            val fullWishText = wishText + appInfoText
            putExtra(Intent.EXTRA_TEXT, fullWishText)

            if (imageUri != null) {
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = getMimeType(imageUri) ?: "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
        }

        val chooserIntent = Intent.createChooser(shareIntent, null)

        withContext(Dispatchers.Main) {
            try {
                context.startActivity(chooserIntent)
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.share_no_app_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private suspend fun downloadImage(context: Context, imageUrl: String): File? =
    withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            if (connection is java.net.HttpURLConnection) {
                if (connection.responseCode !in 200..299) {
                    println("HTTP error code: ${connection.responseCode}")
                    return@withContext null
                }
            }

            val inputStream = connection.getInputStream()

            val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
            val urlPath = url.path
            val fileExtension = urlPath.substringAfterLast('.', "")
            val fileName = "${UUID.randomUUID()}${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"

            val outputFile = File(cacheDir, fileName)
            val outputStream = outputFile.outputStream()

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            outputFile

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

private fun getFileProviderUri(context: Context, file: File): Uri? {
    return try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        println("Ошибка получения FileProvider Uri: ${e.message}")
        null
    }
}

private fun getMimeType(uri: Uri): String? {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
}
