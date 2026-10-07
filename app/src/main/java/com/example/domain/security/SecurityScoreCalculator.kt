package com.example.domain.security

import com.example.domain.model.ScoreDeduction
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecurityFinding
import com.example.domain.model.SecurityScore
import com.example.domain.model.SecuritySeverity
import com.example.domain.model.SecurityStatus

class SecurityScoreCalculator {

    fun calculate(
        checks: List<SecurityCheck>,
        highRiskAppCount: Int = 0,
        isInsecureNetwork: Boolean = false
    ): SecurityScore {
        var score = 100
        val deductions = mutableListOf<ScoreDeduction>()
        var criticalCount = 0
        var warningCount = 0
        var passedCount = 0

        for (check in checks) {
            when (check.severity) {
                SecuritySeverity.CRITICAL -> {
                    criticalCount++
                    val penalty = when (check.id) {
                        "screen_lock" -> 20
                        "device_encryption" -> 10
                        else -> 10
                    }
                    score -= penalty
                    deductions.add(
                        ScoreDeduction(
                            reason = check.title,
                            points = penalty,
                            severity = SecuritySeverity.CRITICAL
                        )
                    )
                }
                SecuritySeverity.WARNING -> {
                    warningCount++
                    val penalty = when (check.id) {
                        "usb_debugging" -> 5
                        "dev_options" -> 2
                        "security_patch" -> 10
                        "unknown_sources" -> 5
                        else -> 3
                    }
                    score -= penalty
                    deductions.add(
                        ScoreDeduction(
                            reason = check.title,
                            points = penalty,
                            severity = SecuritySeverity.WARNING
                        )
                    )
                }
                SecuritySeverity.SECURE -> {
                    passedCount++
                }
                SecuritySeverity.INFO,
                SecuritySeverity.UNAVAILABLE -> {
                    // CRITICAL RULE: DO NOT deduct points simply because Android restricts or does not expose info!
                }
            }
        }

        if (highRiskAppCount > 0) {
            val appPenalty = (highRiskAppCount * 5).coerceAtMost(20)
            score -= appPenalty
            deductions.add(
                ScoreDeduction(
                    reason = "$highRiskAppCount app(s) with elevated sensitive permission risks",
                    points = appPenalty,
                    severity = SecuritySeverity.WARNING
                )
            )
            warningCount += highRiskAppCount
        }

        if (isInsecureNetwork) {
            score -= 5
            deductions.add(
                ScoreDeduction(
                    reason = "Untrusted or open network connection",
                    points = 5,
                    severity = SecuritySeverity.WARNING
                )
            )
            warningCount++
        }

        val finalScore = score.coerceIn(0, 100)
        val status = SecurityStatus.fromScore(finalScore)

        return SecurityScore(
            score = finalScore,
            status = status,
            criticalCount = criticalCount,
            warningCount = warningCount,
            passedCount = passedCount,
            deductions = deductions
        )
    }
}
