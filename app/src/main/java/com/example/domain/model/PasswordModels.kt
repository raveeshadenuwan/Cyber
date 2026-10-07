package com.example.domain.model

enum class PasswordStrength(val label: String, val scoreIndex: Int) {
    VERY_WEAK("Very Weak", 0),
    WEAK("Weak", 1),
    FAIR("Fair", 2),
    STRONG("Strong", 3),
    VERY_STRONG("Very Strong", 4)
}

data class PasswordAnalysisResult(
    val score: Int, // 0 to 100
    val strength: PasswordStrength,
    val crackTimeEstimate: String,
    val feedback: List<String>,
    val length: Int,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigits: Boolean,
    val hasSymbols: Boolean,
    val hasRepeatedChars: Boolean,
    val hasSequentialChars: Boolean,
    val entropyBits: Double
)
