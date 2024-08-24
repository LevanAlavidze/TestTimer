package com.example.testforsmokers.smoke.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CounterUpdateDao {

    @Query("SELECT * FROM counter_updates WHERE id = 1 LIMIT 1")
    suspend fun getCounterUpdate(): CounterUpdate?

    @Insert
    suspend fun insert(counterUpdate: CounterUpdate)

    @Update
    suspend fun update(counterUpdate: CounterUpdate)
}
