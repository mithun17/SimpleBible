package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.database.model.Bible

@Dao
interface BibleDao {

    @Query("SELECT * FROM bible")
    suspend fun getBibles(): List<Bible>

    @Query("SELECT * FROM bible WHERE id=:id LIMIT 1")
    suspend fun getBibleById(id: String): Bible?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBibles(bibles: List<Bible>): List<Long>
}
