package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScan(): Flow<ScanEntity?>

    @Transaction
    @Query("SELECT * FROM scans WHERE id = :scanId")
    fun getScanWithFindings(scanId: Long): Flow<ScanWithFindings?>

    @Query("SELECT * FROM findings WHERE scanId = :scanId")
    fun getFindingsForScan(scanId: Long): Flow<List<FindingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFindings(findings: List<FindingEntity>)

    @Query("DELETE FROM scans WHERE id = :scanId")
    suspend fun deleteScanById(scanId: Long)

    @Query("DELETE FROM scans")
    suspend fun deleteAllScans()
}
