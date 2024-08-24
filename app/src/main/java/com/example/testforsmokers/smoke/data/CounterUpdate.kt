package com.example.testforsmokers.smoke.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counter_updates")
data class CounterUpdate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastDayUpdate: Long,
    val lastWeekUpdate: Int,
    val lastMonthUpdate: Int
)
