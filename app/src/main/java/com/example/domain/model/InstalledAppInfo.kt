package com.example.domain.model

enum class AppRiskLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

data class PermissionDetail(
    val permission: String,
    val simpleName: String,
    val description: String,
    val isGranted: Boolean,
    val isSensitive: Boolean
)

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val targetSdk: Int,
    val isSystemApp: Boolean,
    val requestedPermissions: List<PermissionDetail>,
    val riskLevel: AppRiskLevel,
    val riskReasons: List<String>,
    val installTime: Long = 0L,
    val updateTime: Long = 0L
) {
    val sensitiveGrantedCount: Int get() = requestedPermissions.count { it.isSensitive && it.isGranted }
    val sensitiveRequestedCount: Int get() = requestedPermissions.count { it.isSensitive }
    val sensitivePermissionsList: List<PermissionDetail> get() = requestedPermissions.filter { it.isSensitive }
}
