package com.example

import com.example.domain.model.PasswordStrength
import com.example.domain.security.PasswordAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PasswordAnalyzerTest {

    private lateinit var analyzer: PasswordAnalyzer

    @Before
    fun setUp() {
        analyzer = PasswordAnalyzer()
    }

    @Test
    fun testEmptyPassword() {
        val result = analyzer.analyze("")
        assertEquals(0, result.score)
        assertEquals(PasswordStrength.VERY_WEAK, result.strength)
        assertEquals("Instant", result.crackTimeEstimate)
    }

    @Test
    fun testCommonPassword_password() {
        val result = analyzer.analyze("password")
        assertEquals(PasswordStrength.VERY_WEAK, result.strength)
        assertTrue(result.score < 30)
        assertTrue(result.feedback.any { it.contains("dictionary", ignoreCase = true) })
    }

    @Test
    fun testPassword123() {
        val result = analyzer.analyze("Password123")
        assertTrue(result.hasUppercase)
        assertTrue(result.hasLowercase)
        assertTrue(result.hasDigits)
        assertFalse(result.hasSymbols)
        assertTrue(result.hasSequentialChars) // "123"
        assertTrue(result.score < 60)
    }

    @Test
    fun testPasswordWithSymbols_P_password_123() {
        val result = analyzer.analyze("P@ssword123")
        assertTrue(result.hasSymbols)
        assertTrue(result.hasUppercase)
        assertTrue(result.hasLowercase)
        assertTrue(result.hasDigits)
    }

    @Test
    fun testRandomLongPassword() {
        val result = analyzer.analyze("Kx9#mQ2\$vL8&wP4*zR7!")
        assertTrue(result.length >= 16)
        assertTrue(result.hasUppercase)
        assertTrue(result.hasLowercase)
        assertTrue(result.hasDigits)
        assertTrue(result.hasSymbols)
        assertEquals(PasswordStrength.VERY_STRONG, result.strength)
        assertTrue(result.score >= 80)
    }
}
