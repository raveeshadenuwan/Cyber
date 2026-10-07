package com.example.domain.security

import com.example.domain.model.UrlAnalysisResult
import com.example.domain.model.UrlRiskLevel
import com.example.domain.model.UrlSecurityFlag
import java.net.URI
import java.util.regex.Pattern

class UrlAnalyzer {

    private val ipPattern = Pattern.compile(
        """^(\d{1,3}\.){3}\d{1,3}$"""
    )

    private val suspiciousKeywords = listOf(
        "login", "verify", "banking", "paypal", "secure", "account",
        "wallet", "signin", "auth", "confirm", "recovery", "update-account",
        "security-update", "free-crypto", "airdrop", "lottery", "claim"
    )

    private val shorteners = setOf(
        "bit.ly", "tinyurl.com", "t.co", "is.gd", "ow.ly", "buff.ly",
        "goo.gl", "cutt.ly", "rb.gy", "shorturl.at"
    )

    private val standardPorts = setOf(80, 443, -1)

    fun analyze(inputUrl: String): UrlAnalysisResult {
        val trimmed = inputUrl.trim()
        if (trimmed.isEmpty()) {
            return UrlAnalysisResult(
                url = "",
                host = "",
                scheme = "",
                riskLevel = UrlRiskLevel.UNABLE_TO_DETERMINE,
                verdict = "Empty Input",
                explanation = "Please enter a valid URL to analyze.",
                flags = emptyList()
            )
        }

        // Add scheme if missing for URI parsing
        val normalizedUrl = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }

        val flags = mutableListOf<UrlSecurityFlag>()
        val uri = try {
            URI(normalizedUrl)
        } catch (e: Exception) {
            return UrlAnalysisResult(
                url = trimmed,
                host = "",
                scheme = "",
                riskLevel = UrlRiskLevel.HIGH_RISK,
                verdict = "Malformed URL",
                explanation = "The URL syntax is invalid or improperly formatted.",
                flags = listOf(
                    UrlSecurityFlag(
                        title = "Invalid URL Syntax",
                        description = "Could not parse standard URL components.",
                        isCritical = true
                    )
                )
            )
        }

        val host = uri.host?.lowercase() ?: ""
        val scheme = uri.scheme?.lowercase() ?: ""
        val port = uri.port
        val rawAuthority = uri.rawAuthority ?: ""
        val rawPath = uri.rawPath ?: ""

        // Check 1: HTTPS Scheme
        if (scheme != "https") {
            flags.add(
                UrlSecurityFlag(
                    title = "Insecure Protocol (HTTP)",
                    description = "Traffic is transmitted in plain text and susceptible to interception.",
                    isCritical = false
                )
            )
        }

        // Check 2: IP Address used as host
        if (ipPattern.matcher(host).matches()) {
            flags.add(
                UrlSecurityFlag(
                    title = "Raw IP Address Host",
                    description = "Legitimate websites rarely use raw IP addresses directly instead of registered domain names.",
                    isCritical = true
                )
            )
        }

        // Check 3: Embedded '@' symbol in authority (credential/spoofing trick)
        if (rawAuthority.contains("@")) {
            flags.add(
                UrlSecurityFlag(
                    title = "Authority Contains '@' Symbol",
                    description = "Often used in credential stuffing or URL obfuscation to deceive users about the true destination domain.",
                    isCritical = true
                )
            )
        }

        // Check 4: Punycode (Internationalized Domain Names spoofing)
        if (host.contains("xn--")) {
            flags.add(
                UrlSecurityFlag(
                    title = "Punycode Detected (IDN)",
                    description = "Punycode (xn--) can be used in homograph attacks to mimic legitimate brand names with lookalike Unicode characters.",
                    isCritical = true
                )
            )
        }

        // Check 5: URL Shortener
        if (shorteners.contains(host)) {
            flags.add(
                UrlSecurityFlag(
                    title = "URL Shortening Service",
                    description = "Shortened links conceal the ultimate destination URL until clicked.",
                    isCritical = false
                )
            )
        }

        // Check 6: Excessive Subdomains
        val dotCount = host.count { it == '.' }
        if (dotCount > 3) {
            flags.add(
                UrlSecurityFlag(
                    title = "Excessive Subdomains",
                    description = "Domains with numerous nested subdomains ($dotCount levels) frequently correlate with phishing kits.",
                    isCritical = false
                )
            )
        }

        // Check 7: Suspicious Keywords in host or path
        val matchedKeywords = suspiciousKeywords.filter { keyword ->
            host.contains(keyword) || rawPath.contains(keyword)
        }
        if (matchedKeywords.isNotEmpty()) {
            flags.add(
                UrlSecurityFlag(
                    title = "Security-Sensitive Keywords",
                    description = "Contains sensitive keywords (${matchedKeywords.joinToString(", ")}). Verify that the domain officially belongs to the service provider.",
                    isCritical = false
                )
            )
        }

        // Check 8: Non-standard Port
        if (port !in standardPorts) {
            flags.add(
                UrlSecurityFlag(
                    title = "Non-Standard Port ($port)",
                    description = "The URL connects to an unusual TCP port rather than standard 80 (HTTP) or 443 (HTTPS).",
                    isCritical = false
                )
            )
        }

        // Check 9: Excessive URL length
        if (normalizedUrl.length > 80) {
            flags.add(
                UrlSecurityFlag(
                    title = "Abnormally Long URL",
                    description = "Excessively long URLs (${normalizedUrl.length} characters) may be used to hide malicious parameters or bypass detection.",
                    isCritical = false
                )
            )
        }

        // Determine Risk Level
        val criticalFlags = flags.count { it.isCritical }
        val totalFlags = flags.size

        val (riskLevel, verdict, explanation) = when {
            criticalFlags > 0 || totalFlags >= 3 -> Triple(
                UrlRiskLevel.HIGH_RISK,
                "High Risk Indicators Detected",
                "Multiple high-severity risk signals were found. Proceed with extreme caution and do not enter credentials."
            )
            totalFlags >= 1 -> Triple(
                UrlRiskLevel.SUSPICIOUS,
                "Suspicious Elements Detected",
                "Some heuristic risk factors were identified. Inspect the domain carefully before proceeding."
            )
            host.isNotEmpty() -> Triple(
                UrlRiskLevel.SAFE_LOOKING,
                "No Immediate Heuristic Threats Detected",
                "No suspicious heuristic patterns were detected. Note that heuristic checks cannot guarantee that a website is malware-free."
            )
            else -> Triple(
                UrlRiskLevel.UNABLE_TO_DETERMINE,
                "Indeterminate",
                "Unable to evaluate URL heuristics."
            )
        }

        return UrlAnalysisResult(
            url = normalizedUrl,
            host = host,
            scheme = scheme,
            riskLevel = riskLevel,
            verdict = verdict,
            explanation = explanation,
            flags = flags,
            isHeuristic = true
        )
    }
}
