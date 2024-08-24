package com.example.testforsmokers.smoke.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cigarettes")
data class Cigarette(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long
)