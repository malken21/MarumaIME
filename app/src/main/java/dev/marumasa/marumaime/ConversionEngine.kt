package dev.marumasa.marumaime

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

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
            
            if (response.isNotEmpty()) {
                val firstResult = response[0].jsonArray
                if (firstResult.size > 1) {
                    val words = firstResult[1].jsonArray
                    words.forEach {
                        candidates.add(it.jsonPrimitive.content)
                    }
                }
            }
            
            // Add the original kana as a candidate if it's not already there
            if (!candidates.contains(text)) {
                candidates.add(text)
            }
            candidates.distinct()
        } catch (e: Exception) {
            listOf(text) // Fallback to original text
        }
    }
}
