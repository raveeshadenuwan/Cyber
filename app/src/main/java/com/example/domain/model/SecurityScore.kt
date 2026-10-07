package com.example.domain.model

enum class SecurityStatus(val displayName: String, val minScore: Int, val maxScore: Int) {
    EXCELLENT("Excellent", 90, 100),
    GOOD("Good", 75, 89),
    NEEDS_ATTENTION("Needs Attention", 50, 74),
    POOR("Poor", 25, 49),
    CRITICAL("Critical", 0, 24);

    companion object {
        fun fromScore(score: Int): SecurityStatus {
            val clamped = score.coerceIn(0, 100)
            return when {
                clamped >= 90 -> EXCELLENT
                clamped >= 75 -> GOOD
                clamped >= 50 -> NEEDS_ATTENTION
                clamped >= 25 -> POOR
                else -> CRITICAL
            }
        }
    }
}

data class ScoreDeduction(
    val reason: String,
    val points: Int,
    val severity: SecuritySeverity
)

data class SecurityScore(
    val score: Int,
    val status: SecurityStatus,
    val criticalCount: Int,
    val warningCount: Int,
    val passedCount: Int,
    val deductions: List<ScoreDeduction> = emptyList()
)
