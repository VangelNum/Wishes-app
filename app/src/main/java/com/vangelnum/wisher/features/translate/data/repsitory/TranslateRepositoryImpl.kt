package com.vangelnum.wisher.features.translate.data.repsitory

import com.vangelnum.wisher.features.translate.data.model.api.TranslateApi
import com.vangelnum.wisher.features.translate.domain.repository.TranslateRepository
import jakarta.inject.Inject

class TranslateRepositoryImpl @Inject constructor(
    private val translateApi: TranslateApi
) : TranslateRepository {
    override suspend fun translateText(text: String, langpair: String): String {
        return translateApi.translateText(text, langpair)
    }
}