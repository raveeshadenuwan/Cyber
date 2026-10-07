package com.example.domain.model

data class SecurityCheck(
    val id: String,
    val title: String,
    val subtitle: String,
    val severity: SecuritySeverity,
    val category: String,
    val description: String,
    val actionLabel: String? = null,
    val settingsAction: String? = null
)

data class SecurityFinding(
    val id: String,
    val title: String,
    val description: String,
    val severity: SecuritySeverity,
    val category: String,
    val recommendation: String,
    val actionLabel: String? = null,
    val settingsAction: String? = null
)

data class SecurityScanResult(
    val scanId: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val score: SecurityScore,
    val checks: List<SecurityCheck>,
    val findings: List<SecurityFinding>
)
