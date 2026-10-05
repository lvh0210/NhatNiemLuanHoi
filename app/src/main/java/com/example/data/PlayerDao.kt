package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_state WHERE id = 1 LIMIT 1")
    fun getPlayerState(): Flow<PlayerEntity?>

    @Query("SELECT * FROM player_state WHERE id = 1 LIMIT 1")
    suspend fun getPlayerStateSync(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerState(entity: PlayerEntity)

    @Query("DELETE FROM player_state")
    suspend fun clearPlayerState()
}
