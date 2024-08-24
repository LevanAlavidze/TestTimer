package com.example.testforsmokers.smoke.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CigaretteDao {

    @Insert
    suspend fun insert(cigarette: Cigarette)

    @Query("SELECT COUNT(*) FROM cigarettes WHERE date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getDayCigaretteCount(): Int

    @Query("SELECT COUNT(*) FROM cigarettes WHERE strftime('%W', timestamp / 1000, 'unixepoch') = strftime('%W', 'now')")
    suspend fun getWeekCigaretteCount(): Int

    @Query("SELECT COUNT(*) FROM cigarettes WHERE strftime('%m', timestamp / 1000, 'unixepoch') = strftime('%m', 'now')")
    suspend fun getMonthCigaretteCount(): Int
}
