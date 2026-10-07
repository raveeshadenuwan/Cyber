package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ScanWithFindings(
    @Embedded val scan: ScanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "scanId"
    )
    val findings: List<FindingEntity>
)
