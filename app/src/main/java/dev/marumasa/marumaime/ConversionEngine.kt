package dev.marumasa.marumaime

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ConversionEngine {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun convert(text: String): List<String> = withContext(Dispatchers.IO) {
        if (text.isEmpty()) return@withContext emptyList()
        
        try {
            // Google Japanese Input API (Transliterate)
            // Example response: [["kanji", ["漢字", "感じ", "幹事"]]]
            val response: JsonArray = client.get("https://www.google.com/transliterate") {
                parameter("langpair", "ja-Hira|ja")
                parameter("text", text)
            }.body()

            val candidates = mutableListOf<String>()
            // Always add the original kana as the first candidate
            candidates.add(text)
            
            if (response.isNotEmpty()) {
                val firstResult = response[0].jsonArray
                if (firstResult.size > 1) {
                    val words = firstResult[1].jsonArray
                    words.forEach {
                        val word = it.jsonPrimitive.content
                        if (word != text) {
                            candidates.add(word)
                        }
                    }
                }
            }
            candidates.distinct()
        } catch (e: Exception) {
            listOf(text) // Fallback to original text
        }
    }
}
