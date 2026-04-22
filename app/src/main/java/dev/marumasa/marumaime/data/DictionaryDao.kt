package dev.marumasa.marumaime.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM user_dictionary WHERE reading LIKE :reading || '%'")
    suspend fun findByReading(reading: String): List<DictionaryEntity>

    @Query("SELECT * FROM user_dictionary")
    suspend fun getAll(): List<DictionaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DictionaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DictionaryEntity>)

    @Delete
    suspend fun delete(entry: DictionaryEntity)

    @Query("DELETE FROM user_dictionary")
    suspend fun deleteAll()
}
