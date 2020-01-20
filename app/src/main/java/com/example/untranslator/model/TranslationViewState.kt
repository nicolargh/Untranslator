package com.example.untranslator.model

import com.example.untranslator.data.LanguageCode

data class TranslationViewState(
    val fromCode: LanguageCode = LanguageCode.English,
    val fromText: String = "",
    val toCode: LanguageCode = LanguageCode.English,
    val toText: String = "",
    val progress: Int = 0,
    val numTranslations: Int = 0
) {
    fun onTranslationBegin(fromCode: LanguageCode, fromText: String, numTranslations: Int): TranslationViewState {
        return copy(
            fromCode = fromCode,
            fromText = fromText,
            toText = fromText,
            progress = 0,
            numTranslations = numTranslations
        )
    }

    fun onTranslationProgress(progressCode: LanguageCode, progressText: String, progress: Int): TranslationViewState {
        return copy(
            toCode = progressCode,
            toText = progressText,
            progress = progress
        )
    }
}