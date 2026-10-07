package com.example

import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecuritySeverity
import com.example.domain.model.SecurityStatus
import com.example.domain.security.SecurityScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SecurityScoreCalculatorTest {

    private lateinit var calculator: SecurityScoreCalculator

    @Before
    fun setUp() {
        calculator = SecurityScoreCalculator()
    }

    @Test
    fun testAllSecureChecks_returns100AndExcellent() {
        val checks = listOf(
            SecurityCheck("screen_lock", "Screen Lock", "Secure", SecuritySeverity.SECURE, "Security", "Desc"),
            SecurityCheck("usb_debugging", "ADB", "Disabled", SecuritySeverity.SECURE, "Security", "Desc"),
            SecurityCheck("dev_options", "Dev Options", "Disabled", SecuritySeverity.SECURE, "Security", "Desc")
        )

        val result = calculator.calculate(checks)
        assertEquals(100, result.score)
        assertEquals(SecurityStatus.EXCELLENT, result.status)
        assertEquals(0, result.criticalCount)
        assertEquals(0, result.warningCount)
        assertEquals(3, result.passedCount)
    }

    @Test
    fun testScreenLockMissing_deducts20Points() {
        val checks = listOf(
            SecurityCheck("screen_lock", "Screen Lock", "Missing", SecuritySeverity.CRITICAL, "Security", "Desc")
        )

        val result = calculator.calculate(checks)
        assertEquals(80, result.score)
        assertEquals(SecurityStatus.GOOD, result.status)
        assertEquals(1, result.criticalCount)
    }

    @Test
    fun testUsbDebuggingAndDevOptionsEnabled() {
        val checks = listOf(
            SecurityCheck("screen_lock", "Screen Lock", "Secure", SecuritySeverity.SECURE, "Security", "Desc"),
            SecurityCheck("usb_debugging", "ADB", "Active", SecuritySeverity.WARNING, "Security", "Desc"),
            SecurityCheck("dev_options", "Dev Options", "Active", SecuritySeverity.WARNING, "Security", "Desc")
        )

        val result = calculator.calculate(checks)
        // 100 - 5 (usb) - 2 (dev_options) = 93
        assertEquals(93, result.score)
        assertEquals(SecurityStatus.EXCELLENT, result.status)
        assertEquals(2, result.warningCount)
    }

    @Test
    fun testUnavailableChecks_neverDeductPoints() {
        val checks = listOf(
            SecurityCheck("screen_lock", "Screen Lock", "Secure", SecuritySeverity.SECURE, "Security", "Desc"),
            SecurityCheck("patch", "Patch", "Unavailable", SecuritySeverity.UNAVAILABLE, "Security", "Desc"),
            SecurityCheck("play_protect", "Play Protect", "Restricted", SecuritySeverity.INFO, "Security", "Desc")
        )

        val result = calculator.calculate(checks)
        assertEquals(100, result.score)
    }

    @Test
    fun testMultipleCriticalIssues_clampsToValidRange() {
        val checks = listOf(
            SecurityCheck("screen_lock", "Screen Lock", "Missing", SecuritySeverity.CRITICAL, "Security", "Desc"),
            SecurityCheck("device_encryption", "Encryption", "Missing", SecuritySeverity.CRITICAL, "Security", "Desc"),
            SecurityCheck("usb_debugging", "ADB", "Active", SecuritySeverity.WARNING, "Security", "Desc"),
            SecurityCheck("security_patch", "Patch", "Outdated", SecuritySeverity.WARNING, "Security", "Desc")
        )

        val result = calculator.calculate(checks, highRiskAppCount = 10, isInsecureNetwork = true)
        assertTrue(result.score in 0..100)
    }
}
