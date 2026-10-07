package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "findings",
    foreignKeys = [
        ForeignKey(
            entity = ScanEntity::class,
            parentColumns = ["id"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["scanId"])]
)
data class FindingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanId: Long,
    val title: String,
    val description: String,
    val severity: String,
    val category: String,
    val recommendation: String
)
