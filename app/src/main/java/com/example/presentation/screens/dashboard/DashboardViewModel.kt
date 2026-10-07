package com.example.presentation.screens.dashboard

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AppRepository
import com.example.data.repository.NetworkRepository
import com.example.data.repository.SecurityScannerRepository
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecurityFinding
import com.example.domain.model.SecurityScore
import com.example.domain.security.SecurityScanner
import com.example.domain.security.SecurityScoreCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val score: SecurityScore? = null,
    val lastScanTimestamp: Long? = null,
    // Device Scanner Metrics
    val checks: List<SecurityCheck> = emptyList(),
    val findings: List<SecurityFinding> = emptyList(),
    // Permission Monitor Metrics
    val totalAppsCount: Int = 0,
    val highRiskAppsCount: Int = 0,
    val mediumRiskAppsCount: Int = 0,
    val lowRiskAppsCount: Int = 0,
    val sensitiveAppsCount: Int = 0,
    val topRiskApps: List<InstalledAppInfo> = emptyList(),
    // Network Analyzer Metrics
    val networkInfo: NetworkSecurityInfo? = null,
    val errorMessage: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SecurityScannerRepository(application)
    private val appRepository = AppRepository(application)
    private val networkRepository = NetworkRepository(application)
    private val scanner = SecurityScanner(application)
    private val calculator = SecurityScoreCalculator()

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val latestScanFlow = repository.getLatestScan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                coroutineScope {
                    // Fetch all three metric hubs concurrently
                    val systemDeferred = async { scanner.performSystemChecks() }
                    val appsDeferred = async { appRepository.getInstalledApps() }
                    val networkDeferred = async { networkRepository.getNetworkInfo() }

                    val (systemChecks, systemFindings) = systemDeferred.await()
                    val apps = appsDeferred.await()
                    val networkInfo = networkDeferred.await()

                    val highRiskApps = apps.filter { it.riskLevel == AppRiskLevel.HIGH }
                    val mediumRiskApps = apps.filter { it.riskLevel == AppRiskLevel.MEDIUM }
                    val lowRiskApps = apps.filter { it.riskLevel == AppRiskLevel.LOW }
                    val sensitiveApps = apps.filter { it.sensitiveRequestedCount > 0 }

                    val isInsecureNetwork = !networkInfo.isConnected || (networkInfo.warnings.isNotEmpty() && !networkInfo.isVpnActive)

                    val holisticScore = calculator.calculate(
                        checks = systemChecks,
                        highRiskAppCount = highRiskApps.size,
                        isInsecureNetwork = isInsecureNetwork
                    )

                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        score = holisticScore,
                        lastScanTimestamp = System.currentTimeMillis(),
                        checks = systemChecks,
                        findings = systemFindings,
                        totalAppsCount = apps.size,
                        highRiskAppsCount = highRiskApps.size,
                        mediumRiskAppsCount = mediumRiskApps.size,
                        lowRiskAppsCount = lowRiskApps.size,
                        sensitiveAppsCount = sensitiveApps.size,
                        topRiskApps = highRiskApps.take(3),
                        networkInfo = networkInfo,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to inspect central telemetry: ${e.localizedMessage}"
                )
            }
        }
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
