package com.example.domain.model

data class SecurityRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val severity: SecuritySeverity,
    val actionLabel: String,
    val settingsAction: String? = null
)
