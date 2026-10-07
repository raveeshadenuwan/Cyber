package com.example.presentation.screens.apps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AppRepository
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PermissionFilterCategory(val displayName: String) {
    ALL("All Permissions"),
    LOCATION("Location"),
    CAMERA("Camera"),
    MICROPHONE("Microphone"),
    SMS_CONTACTS("SMS / Contacts"),
    STORAGE("Storage / Media"),
    PHONE("Phone State")
}

enum class AppSortOrder(val displayName: String) {
    BY_RISK_DESC("Highest Risk"),
    BY_SENSITIVE_COUNT_DESC("Most Sensitive Perms"),
    BY_NAME_ASC("Name (A–Z)")
}

data class AppsUiState(
    val isLoading: Boolean = true,
    val allApps: List<InstalledAppInfo> = emptyList(),
    val filteredApps: List<InstalledAppInfo> = emptyList(),
    val searchQuery: String = "",
    val selectedRiskFilter: AppRiskLevel? = null,
    val selectedPermissionFilter: PermissionFilterCategory = PermissionFilterCategory.ALL,
    val selectedSortOrder: AppSortOrder = AppSortOrder.BY_RISK_DESC,
    val onlyWithSensitivePerms: Boolean = false,
    val errorMessage: String? = null
) {
    val totalAppsCount: Int get() = allApps.size
    val highRiskCount: Int get() = allApps.count { it.riskLevel == AppRiskLevel.HIGH }
    val mediumRiskCount: Int get() = allApps.count { it.riskLevel == AppRiskLevel.MEDIUM }
    val lowRiskCount: Int get() = allApps.count { it.riskLevel == AppRiskLevel.LOW }
    val sensitiveAppsCount: Int get() = allApps.count { it.sensitiveRequestedCount > 0 }
}

class AppsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val apps = repository.getInstalledApps()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allApps = apps,
                    filteredApps = computeFilteredApps(
                        apps = apps,
                        query = _uiState.value.searchQuery,
                        risk = _uiState.value.selectedRiskFilter,
                        permCategory = _uiState.value.selectedPermissionFilter,
                        sort = _uiState.value.selectedSortOrder,
                        onlySensitive = _uiState.value.onlyWithSensitivePerms
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load apps: ${e.localizedMessage}"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredApps = computeFilteredApps(
                apps = _uiState.value.allApps,
                query = query,
                risk = _uiState.value.selectedRiskFilter,
                permCategory = _uiState.value.selectedPermissionFilter,
                sort = _uiState.value.selectedSortOrder,
                onlySensitive = _uiState.value.onlyWithSensitivePerms
            )
        )
    }

    fun onFilterRiskSelected(risk: AppRiskLevel?) {
        _uiState.value = _uiState.value.copy(
            selectedRiskFilter = risk,
            filteredApps = computeFilteredApps(
                apps = _uiState.value.allApps,
                query = _uiState.value.searchQuery,
                risk = risk,
                permCategory = _uiState.value.selectedPermissionFilter,
                sort = _uiState.value.selectedSortOrder,
                onlySensitive = _uiState.value.onlyWithSensitivePerms
            )
        )
    }

    fun onPermissionCategorySelected(category: PermissionFilterCategory) {
        _uiState.value = _uiState.value.copy(
            selectedPermissionFilter = category,
            filteredApps = computeFilteredApps(
                apps = _uiState.value.allApps,
                query = _uiState.value.searchQuery,
                risk = _uiState.value.selectedRiskFilter,
                permCategory = category,
                sort = _uiState.value.selectedSortOrder,
                onlySensitive = _uiState.value.onlyWithSensitivePerms
            )
        )
    }

    fun onSortOrderSelected(sortOrder: AppSortOrder) {
        _uiState.value = _uiState.value.copy(
            selectedSortOrder = sortOrder,
            filteredApps = computeFilteredApps(
                apps = _uiState.value.allApps,
                query = _uiState.value.searchQuery,
                risk = _uiState.value.selectedRiskFilter,
                permCategory = _uiState.value.selectedPermissionFilter,
                sort = sortOrder,
                onlySensitive = _uiState.value.onlyWithSensitivePerms
            )
        )
    }

    fun toggleOnlySensitive(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            onlyWithSensitivePerms = enabled,
            filteredApps = computeFilteredApps(
                apps = _uiState.value.allApps,
                query = _uiState.value.searchQuery,
                risk = _uiState.value.selectedRiskFilter,
                permCategory = _uiState.value.selectedPermissionFilter,
                sort = _uiState.value.selectedSortOrder,
                onlySensitive = enabled
            )
        )
    }

    fun getAppByPackageName(packageName: String): InstalledAppInfo? {
        return _uiState.value.allApps.firstOrNull { it.packageName == packageName }
    }

    private fun computeFilteredApps(
        apps: List<InstalledAppInfo>,
        query: String,
        risk: AppRiskLevel?,
        permCategory: PermissionFilterCategory,
        sort: AppSortOrder,
        onlySensitive: Boolean
    ): List<InstalledAppInfo> {
        return apps.asSequence()
            .filter { app ->
                val matchesQuery = query.isEmpty() ||
                    app.appName.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)

                val matchesRisk = risk == null || app.riskLevel == risk

                val matchesSensitiveRequirement = !onlySensitive || app.sensitiveRequestedCount > 0

                val matchesPermCategory = when (permCategory) {
                    PermissionFilterCategory.ALL -> true
                    PermissionFilterCategory.LOCATION -> app.requestedPermissions.any {
                        it.permission.contains("LOCATION", ignoreCase = true) && it.isGranted
                    }
                    PermissionFilterCategory.CAMERA -> app.requestedPermissions.any {
                        it.permission.contains("CAMERA", ignoreCase = true) && it.isGranted
                    }
                    PermissionFilterCategory.MICROPHONE -> app.requestedPermissions.any {
                        it.permission.contains("RECORD_AUDIO", ignoreCase = true) && it.isGranted
                    }
                    PermissionFilterCategory.SMS_CONTACTS -> app.requestedPermissions.any {
                        (it.permission.contains("SMS", ignoreCase = true) || it.permission.contains("CONTACTS", ignoreCase = true)) && it.isGranted
                    }
                    PermissionFilterCategory.STORAGE -> app.requestedPermissions.any {
                        (it.permission.contains("STORAGE", ignoreCase = true) || it.permission.contains("MEDIA", ignoreCase = true)) && it.isGranted
                    }
                    PermissionFilterCategory.PHONE -> app.requestedPermissions.any {
                        (it.permission.contains("PHONE", ignoreCase = true) || it.permission.contains("CALL_LOG", ignoreCase = true)) && it.isGranted
                    }
                }

                matchesQuery && matchesRisk && matchesSensitiveRequirement && matchesPermCategory
            }
            .sortedWith { a, b ->
                when (sort) {
                    AppSortOrder.BY_RISK_DESC -> {
                        val riskCompare = a.riskLevel.ordinal.compareTo(b.riskLevel.ordinal)
                        if (riskCompare != 0) riskCompare
                        else b.sensitiveGrantedCount.compareTo(a.sensitiveGrantedCount)
                    }
                    AppSortOrder.BY_SENSITIVE_COUNT_DESC -> {
                        val countCompare = b.sensitiveGrantedCount.compareTo(a.sensitiveGrantedCount)
                        if (countCompare != 0) countCompare
                        else a.riskLevel.ordinal.compareTo(b.riskLevel.ordinal)
                    }
                    AppSortOrder.BY_NAME_ASC -> a.appName.compareTo(b.appName, ignoreCase = true)
                }
            }
            .toList()
    }
}
