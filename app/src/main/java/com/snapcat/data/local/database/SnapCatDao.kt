package com.snapcat.data.local.database

import androidx.room.Dao
import androidx.room.Query
import com.snapcat.data.model.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapCatDao {
    @Query("SELECT * FROM cat")
    fun getCat(): Flow<List<Cat>>
}