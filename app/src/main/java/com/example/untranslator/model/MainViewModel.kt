package com.example.untranslator.model

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.example.untranslator.data.LanguageCode
import com.example.untranslator.data.TranslationApi
import com.example.untranslator.data.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        if (action.text.isBlank()) return

        liveData.postValue(
            liveData.value?.onTranslationBegin(
                fromCode = action.fromCode,
                fromText = action.text,
                numTranslations = action.numTranslations
            )
        )

        viewModelScope.launch {
            var text = action.text
            var from = action.fromCode
            var to = LanguageCode.values().random()
            (1 until action.numTranslations).forEach lit@{
                when (val result = getTranslation(text, from.code, to.code)) {
                    is TranslationResult.Error -> {
                        postTranslationError(result.isApiError)
                        return@lit
                    }
                    is TranslationResult.Success -> {
                        postTranslationSuccess(to, result.text, it)
                        text = result.text
                        from = to
                        to = LanguageCode.values().random()
                    }
                }
            }
            when (val finalResult = getTranslation(text, from.code, action.toCode.code)) {
                is TranslationResult.Error -> postTranslationError(finalResult.isApiError)
                is TranslationResult.Success -> postTranslationSuccess(
                    action.toCode,
                    finalResult.text,
                    action.numTranslations
                )
            }
        }
    }

    private suspend fun getTranslation(text: String, from: String, to: String): TranslationResult {
        return withContext(Dispatchers.Default) {
            translationApi.translate(text, from, to)
        }
    }

    private fun postTranslationError(isApiError: Boolean) {
        liveData.postValue(
            liveData.value?.onTranslationError(isApiError)
        )
    }

    private fun postTranslationSuccess(toCode: LanguageCode, text: String, progress: Int) {
        liveData.postValue(
            liveData.value?.onTranslationProgress(toCode, text, progress)
        )
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