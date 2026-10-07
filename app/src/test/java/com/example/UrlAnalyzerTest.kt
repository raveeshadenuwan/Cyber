package com.example

import com.example.domain.model.UrlRiskLevel
import com.example.domain.security.UrlAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UrlAnalyzerTest {

    private lateinit var analyzer: UrlAnalyzer

    @Before
    fun setUp() {
        analyzer = UrlAnalyzer()
    }

    @Test
    fun testStandardHttpsGoogle() {
        val result = analyzer.analyze("https://google.com")
        assertEquals(UrlRiskLevel.SAFE_LOOKING, result.riskLevel)
        assertTrue(result.isHeuristic)
        assertEquals(0, result.flags.size)
    }

    @Test
    fun testInsecureHttpExample() {
        val result = analyzer.analyze("http://example.com")
        assertEquals(UrlRiskLevel.SUSPICIOUS, result.riskLevel)
        assertTrue(result.flags.any { it.title.contains("HTTP") })
    }

    @Test
    fun testRawIpHostWithLogin() {
        val result = analyzer.analyze("http://192.168.1.1/login")
        assertEquals(UrlRiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.flags.any { it.title.contains("IP Address") })
    }

    @Test
    fun testAuthorityContainingAtSymbol() {
        val result = analyzer.analyze("http://google.com@phishing-target.net/verify")
        assertEquals(UrlRiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.flags.any { it.title.contains("@") })
    }

    @Test
    fun testPunycodeHomograph() {
        val result = analyzer.analyze("https://xn--pple-43d.com")
        assertEquals(UrlRiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.flags.any { it.title.contains("Punycode") })
    }

    @Test
    fun testSuspiciousKeywordsUrl() {
        val result = analyzer.analyze("https://secure-login-example.com")
        assertEquals(UrlRiskLevel.SUSPICIOUS, result.riskLevel)
        assertTrue(result.flags.any { it.title.contains("Keywords") })
    }

    @Test
    fun testVeryLongUrl() {
        val longUrl = "https://example.com/" + "a".repeat(120)
        val result = analyzer.analyze(longUrl)
        assertTrue(result.flags.any { it.title.contains("Long") })
    }

    @Test
    fun testUrlShortener() {
        val result = analyzer.analyze("https://bit.ly/update-now")
        assertTrue(result.flags.any { it.title.contains("Shorten") })
    }
}
