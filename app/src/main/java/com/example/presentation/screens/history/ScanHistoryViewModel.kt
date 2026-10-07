package com.example.presentation.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScanEntity
import com.example.data.repository.SecurityScannerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SecurityScannerRepository(application)

    val scansHistory: StateFlow<List<ScanEntity>> = repository.getScanHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteScan(scanId: Long) {
        viewModelScope.launch {
            repository.deleteScan(scanId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }
}
