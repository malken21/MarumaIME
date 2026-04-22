package dev.marumasa.marumaime.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_dictionary")
data class DictionaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reading: String,
    val word: String,
    val category: String = "名詞"
)
