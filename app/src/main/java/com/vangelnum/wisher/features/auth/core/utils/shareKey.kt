package com.vangelnum.wisher.features.auth.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
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
    scope.launch { // Запускаем корутину в предоставленной области видимости
        val imageUri: Uri? = try {
            // 1. Скачиваем изображение в фоновом потоке (IO)
            val downloadedFile = downloadImage(context, wishImage)
            if (downloadedFile == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Не удалось скачать изображение для отправки", Toast.LENGTH_SHORT).show()
                }
                return@launch // Прекращаем выполнение, если скачивание не удалось
            }

            // 2. Получаем URI для скачанного файла через FileProvider
            getFileProviderUri(context, downloadedFile)

        } catch (e: Exception) {
            // Обработка исключений при скачивании или обработке файла
            e.printStackTrace() // Логируем ошибку
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка подготовки изображения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            return@launch // Прекращаем выполнение при ошибке
        }

        // 3. Создаем Intent для отправки
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, wishText) // Добавляем текст пожелания

            if (imageUri != null) {
                putExtra(Intent.EXTRA_STREAM, imageUri) // Добавляем URI изображения
                // Определяем MIME-тип на основе расширения файла
                type = getMimeType(imageUri) ?: "image/*" // Используем image/* если тип не удалось определить
                // Предоставляем временное разрешение на чтение URI принимающему приложению
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                // Если изображение не удалось получить, отправляем только текст
                type = "text/plain"
            }
        }

        // 4. Создаем и показываем стандартное диалоговое окно Sharesheet
        val chooserIntent = Intent.createChooser(shareIntent, null) // null - использовать заголовок по умолчанию

        // 5. Запускаем активность (должно происходить в основном потоке UI)
        withContext(Dispatchers.Main) {
            try {
                context.startActivity(chooserIntent)
            } catch (e: Exception) {
                // Обработка случая, когда нет приложений, способных обработать Intent
                Toast.makeText(context, "Не найдено приложение для отправки контента", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Вспомогательная функция для скачивания изображения в фоновом потоке
private suspend fun downloadImage(context: Context, imageUrl: String): File? =
    withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            // Проверка кода ответа для HTTP(S) URL
            if (connection is java.net.HttpURLConnection) {
                if (connection.responseCode !in 200..299) {
                    println("HTTP error code: ${connection.responseCode}")
                    return@withContext null // Скачивание не удалось
                }
            }

            val inputStream = connection.getInputStream()

            // Создаем директорию и файл для сохранения
            val cacheDir = File(context.cacheDir, "images").apply { mkdirs() } // Убедимся, что директория существует
            // Создаем уникальное имя файла, пытаясь сохранить расширение
            val urlPath = url.path
            val fileExtension = urlPath.substringAfterLast('.', "")
            val fileName = "${UUID.randomUUID()}${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"

            val outputFile = File(cacheDir, fileName)
            val outputStream = outputFile.outputStream()

            // Копируем данные
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            outputFile // Возвращаем сохраненный файл

        } catch (e: Exception) {
            e.printStackTrace()
            null // Возвращаем null при ошибке
        }
    }

// Вспомогательная функция для получения URI через FileProvider
private fun getFileProviderUri(context: Context, file: File): Uri? {
    return try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Убедитесь, что authority соответствует вашему манифесту
            file
        )
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        println("Ошибка получения FileProvider Uri: ${e.message}")
        null // Возвращаем null, если не удалось сгенерировать URI
    }
}

private fun getMimeType(uri: Uri): String? {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
}