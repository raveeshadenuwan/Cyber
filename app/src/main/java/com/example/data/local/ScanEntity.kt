package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val score: Int,
    val status: String,
    val criticalCount: Int,
    val warningCount: Int,
    val passedCount: Int,
    val summary: String
)
