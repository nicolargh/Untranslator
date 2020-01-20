package com.example.untranslator.model

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.example.untranslator.data.LanguageCode
import com.example.untranslator.data.TranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TranslationAction(
    val fromCode: LanguageCode,
    val toCode: LanguageCode,
    val text: String,
    val numTranslations: Int
)

class MainViewModel(private val translationApi: TranslationApi) : ViewModel() {
    val liveData = MutableLiveData<TranslationViewState>(TranslationViewState())

    fun dispatchTranslation(action: TranslationAction) {
        translate(action)
    }

    private fun translate(action: TranslationAction) {
        viewModelScope.launch {
            var text = action.text
            var from = action.fromCode
            var to = LanguageCode.values().random()
            (0 until action.numTranslations - 1).forEach { _ ->
                text = getTranslation(text, from.code, to.code)
                from = to
                to = LanguageCode.values().random()
            }
            text = getTranslation(text, from.code, action.toCode.code)

            liveData.postValue(
                TranslationViewState(
                    fromCode = action.fromCode,
                    fromText = action.text,
                    toCode = action.toCode,
                    toText = text,
                    numTranslations = action.numTranslations
                )
            )
        }
    }

    private suspend fun getTranslation(text: String, from: String, to: String): String {
        return withContext(Dispatchers.Default) {
            translationApi.translate(text, from, to)
        }
    }

    class Factory : ViewModelProvider.Factory {
        fun getViewModel(activity: FragmentActivity): MainViewModel {
            return ViewModelProviders.of(activity, this).get(MainViewModel::class.java)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(TranslationApi()) as T
        }
    }
}