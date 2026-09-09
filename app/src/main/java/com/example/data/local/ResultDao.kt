package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Query("SELECT * FROM saved_results ORDER BY timestamp DESC")
    fun getAllSavedResults(): Flow<List<SavedResultEntity>>

    @Query("SELECT * FROM saved_results WHERE rollNo = :rollNo ORDER BY semester ASC")
    fun getResultsByRollNo(rollNo: String): Flow<List<SavedResultEntity>>

    @Query("SELECT * FROM saved_results WHERE id = :id LIMIT 1")
    suspend fun getResultById(id: Long): SavedResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: SavedResultEntity): Long

    @Query("DELETE FROM saved_results WHERE id = :id")
    suspend fun deleteResultById(id: Long)

    @Query("DELETE FROM saved_results")
    suspend fun deleteAll()
}
