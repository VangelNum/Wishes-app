package com.vangelnum.wisher.features.translate.domain.repository

interface TranslateRepository {
    suspend fun translateText(text: String, langpair: String): String
}