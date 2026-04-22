package dev.marumasa.marumaime.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "next_word_prediction",
    indices = [Index(value = ["prevWord", "nextWord"], unique = true)]
)
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prevWord: String,
    val nextWord: String,
    val count: Int = 1
)
