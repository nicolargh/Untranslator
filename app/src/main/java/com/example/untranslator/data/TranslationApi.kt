package com.example.untranslator.data

import android.os.Build
import android.text.Html
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

sealed class TranslationResult {
    class Success(val text: String) : TranslationResult()
    object Error : TranslationResult()
}

class TranslationApi {
    companion object {
        // google apps script of mine that handles translation
        // view script here: https://script.google.com/d/1YrUKgBhlEAkn1zRLAk5j5qGmR5gTNKNLuAtBvxKXIFwFIFYbaZR9dpaE/edit?usp=sharing
        // found on stack overflow: https://stackoverflow.com/a/48159904/12745149
        private const val TRANSLATE_URL =
            "https://script.google.com/macros/s/AKfycbygX9ACBLFFp2acIqzBa6Sl36ZYioq8fq3C5e8xo0rVzOffJjNz/exec"
        private const val ERROR_IDENTIFIER = "<title>Error</title>"
    }

    suspend fun translate(text: String, from: String, to: String): TranslationResult {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val url = URL(
            "$TRANSLATE_URL?q=$encodedText&target=$to&source=$from"
        )
        return try {
            getResponse(url)
        } catch (exception: Exception) {
            TranslationResult.Error
        }
    }

    private suspend fun getResponse(url: URL): TranslationResult {
        val response = StringBuilder()
        withContext(Dispatchers.IO) {
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            val readIn = BufferedReader(InputStreamReader(connection.inputStream))
            var inputLine: String?
            while (readIn.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            readIn.close()
        }

        return if (response.toString().contains(ERROR_IDENTIFIER)) {
            TranslationResult.Error
        } else {
            TranslationResult.Success(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(response.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
                } else {
                    Html.fromHtml(response.toString()).toString()
                }
            )
        }
    }

}