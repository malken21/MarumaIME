package dev.marumasa.marumaime.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PredictionDao {
    @Query("SELECT * FROM next_word_prediction WHERE prevWord = :prevWord ORDER BY count DESC LIMIT 10")
    suspend fun getPredictions(prevWord: String): List<PredictionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(prediction: PredictionEntity): Long

    @Query("UPDATE next_word_prediction SET count = count + 1 WHERE prevWord = :prevWord AND nextWord = :nextWord")
    suspend fun incrementCount(prevWord: String, nextWord: String)

    @Transaction
    suspend fun learn(prevWord: String, nextWord: String) {
        val id = insert(PredictionEntity(prevWord = prevWord, nextWord = nextWord))
        if (id == -1L) {
            incrementCount(prevWord, nextWord)
        }
    }
}
