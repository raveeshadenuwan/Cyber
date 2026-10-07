package com.example.presentation.screens.scan

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.SecurityScannerRepository
import com.example.domain.model.SecurityScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScanState {
    object Idle : ScanState()
    data class Scanning(val phaseText: String, val progress: Float) : ScanState()
    data class Completed(val result: SecurityScanResult) : ScanState()
    data class Error(val message: String) : ScanState()
}

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SecurityScannerRepository(application)

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun startScan() {
        viewModelScope.launch {
            try {
                _scanState.value = ScanState.Scanning("Initializing security audit...", 0.05f)
                val result = repository.runFullSecurityScan { phase, progress ->
                    _scanState.value = ScanState.Scanning(phase, progress)
                }
                _scanState.value = ScanState.Completed(result)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("Scan failed: ${e.localizedMessage}")
            }
        }
    }

    fun resetScan() {
        _scanState.value = ScanState.Idle
    }

    fun openSystemSettings(action: String) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                getApplication<Application>().startActivity(fallbackIntent)
            } catch (_: Exception) {}
        }
    }
}
