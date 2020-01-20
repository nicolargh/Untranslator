package com.example.untranslator.model

import com.example.untranslator.data.LanguageCode

data class TranslationViewState(
    val fromCode: LanguageCode = LanguageCode.English,
    val fromText: String = "",
    val toCode: LanguageCode = LanguageCode.English,
    val toText: String = "",
    val numTranslations: Int = 1
)