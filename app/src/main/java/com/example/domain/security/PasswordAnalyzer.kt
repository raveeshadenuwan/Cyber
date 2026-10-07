package com.example.domain.security

import com.example.domain.model.PasswordAnalysisResult
import com.example.domain.model.PasswordStrength
import kotlin.math.log2
import kotlin.math.pow

class PasswordAnalyzer {

    private val commonPasswords = setOf(
        "password", "123456", "12345678", "123456789", "qwerty", "12345", "password123",
        "admin", "1234567", "letmein", "welcome", "login", "iloveyou", "master",
        "computer", "secret", "pass123", "abc123", "monkey", "dragon"
    )

    private val commonSequences = listOf(
        "123", "234", "345", "456", "567", "678", "789", "890",
        "abc", "bcd", "cde", "def", "efg", "fgh", "ghi",
        "qwe", "wer", "ert", "asd", "sdf", "zxc"
    )

    fun analyze(password: String): PasswordAnalysisResult {
        if (password.isEmpty()) {
            return PasswordAnalysisResult(
                score = 0,
                strength = PasswordStrength.VERY_WEAK,
                crackTimeEstimate = "Instant",
                feedback = listOf("Enter a password to analyze its strength."),
                length = 0,
                hasUppercase = false,
                hasLowercase = false,
                hasDigits = false,
                hasSymbols = false,
                hasRepeatedChars = false,
                hasSequentialChars = false,
                entropyBits = 0.0
            )
        }

        val length = password.length
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        // Check repeated characters (e.g., "aaa" or "111")
        val hasRepeated = Regex("""(.)\1{2,}""").containsMatchIn(password)

        // Check sequential characters
        val lowerPassword = password.lowercase()
        val hasSequential = commonSequences.any { lowerPassword.contains(it) }

        // Check common word match
        val isCommon = commonPasswords.contains(lowerPassword)

        val feedback = mutableListOf<String>()

        var poolSize = 0
        if (hasLower) poolSize += 26
        if (hasUpper) poolSize += 26
        if (hasDigit) poolSize += 10
        if (hasSymbol) poolSize += 33

        val entropy = if (poolSize > 0) length * log2(poolSize.toDouble()) else 0.0

        var score = 0

        // Length contribution
        when {
            length >= 16 -> score += 35
            length >= 12 -> score += 25
            length >= 8 -> score += 15
            else -> {
                score += 5
                feedback.add("Password is too short. Use at least 12 to 16 characters.")
            }
        }

        // Diversity contribution
        var varietyCount = 0
        if (hasUpper) { score += 15; varietyCount++ } else feedback.add("Add uppercase letters (A-Z).")
        if (hasLower) { score += 10; varietyCount++ } else feedback.add("Add lowercase letters (a-z).")
        if (hasDigit) { score += 15; varietyCount++ } else feedback.add("Add numbers (0-9).")
        if (hasSymbol) { score += 25; varietyCount++ } else feedback.add("Add special symbols (!@#$%^&*).")

        // Penalties
        if (hasRepeated) {
            score -= 15
            feedback.add("Avoid repeating consecutive characters (e.g. 'aaa').")
        }

        if (hasSequential) {
            score -= 15
            feedback.add("Avoid sequential patterns (e.g. '1234', 'qwerty').")
        }

        if (isCommon) {
            score -= 40
            feedback.add("This is a commonly used, well-known dictionary password!")
        }

        val clampedScore = score.coerceIn(0, 100)

        val strength = when {
            clampedScore >= 80 -> PasswordStrength.VERY_STRONG
            clampedScore >= 60 -> PasswordStrength.STRONG
            clampedScore >= 40 -> PasswordStrength.FAIR
            clampedScore >= 20 -> PasswordStrength.WEAK
            else -> PasswordStrength.VERY_WEAK
        }

        val crackTime = estimateCrackTime(entropy, isCommon)

        return PasswordAnalysisResult(
            score = clampedScore,
            strength = strength,
            crackTimeEstimate = crackTime,
            feedback = feedback,
            length = length,
            hasUppercase = hasUpper,
            hasLowercase = hasLower,
            hasDigits = hasDigit,
            hasSymbols = hasSymbol,
            hasRepeatedChars = hasRepeated,
            hasSequentialChars = hasSequential,
            entropyBits = entropy
        )
    }

    private fun estimateCrackTime(entropy: Double, isCommon: Boolean): String {
        if (isCommon) return "Instant (< 1 second)"
        return when {
            entropy < 28 -> "Instant (< 1 second)"
            entropy < 36 -> "A few seconds"
            entropy < 50 -> "A few minutes to hours"
            entropy < 65 -> "Several months to years"
            entropy < 80 -> "Centuries"
            else -> "Millions of years"
        }
    }
}
