package com.example.domain.model

enum class SecuritySeverity {
    CRITICAL,
    WARNING,
    INFO,
    SECURE,
    UNAVAILABLE;

    val label: String
        get() = when (this) {
            CRITICAL -> "Critical"
            WARNING -> "Warning"
            INFO -> "Info"
            SECURE -> "Secure"
            UNAVAILABLE -> "Unavailable"
        }
}
