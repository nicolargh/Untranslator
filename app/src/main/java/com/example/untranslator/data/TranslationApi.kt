package com.example.untranslator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder


class TranslationApi {
    companion object {
        // google apps script of mine that handles translation
        // view script here: https://script.google.com/d/1YrUKgBhlEAkn1zRLAk5j5qGmR5gTNKNLuAtBvxKXIFwFIFYbaZR9dpaE/edit?usp=sharing
        private const val TRANSLATE_URL =
            "https://script.google.com/macros/s/AKfycbygX9ACBLFFp2acIqzBa6Sl36ZYioq8fq3C5e8xo0rVzOffJjNz/exec"
    }

    suspend fun translate(text: String, from: String, to: String): String {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val url = URL(
            "$TRANSLATE_URL?q=$encodedText&target=$to&source=$from"
        )
        return getResponse(url)
    }

    private suspend fun getResponse(url: URL): String {
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

        return response.toString()
    }

}