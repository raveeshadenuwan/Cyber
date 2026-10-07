package com.example.domain.model

enum class UrlRiskLevel(val label: String) {
    SAFE_LOOKING("Safe-Looking"),
    SUSPICIOUS("Suspicious"),
    HIGH_RISK("High Risk"),
    UNABLE_TO_DETERMINE("Unable to Determine")
}

data class UrlSecurityFlag(
    val title: String,
    val description: String,
    val isCritical: Boolean
)

data class UrlAnalysisResult(
    val url: String,
    val host: String,
    val scheme: String,
    val riskLevel: UrlRiskLevel,
    val verdict: String,
    val explanation: String,
    val flags: List<UrlSecurityFlag>,
    val isHeuristic: Boolean = true
)
