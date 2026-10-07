package com.example.domain.security

import java.security.SecureRandom
import kotlin.math.log2

enum class GeneratorPreset(
    val title: String,
    val description: String,
    val defaultLength: Int,
    val upper: Boolean,
    val lower: Boolean,
    val digits: Boolean,
    val symbols: Boolean,
    val excludeAmbiguous: Boolean
) {
    RECOMMENDED("Recommended", "20 chars with all character sets", 20, true, true, true, true, false),
    MAX_SECURITY("Maximum Security", "32 chars for master passwords & vaults", 32, true, true, true, true, false),
    EASY_TO_READ("Easy to Read", "Excludes lookalike chars (0, O, 1, l, I)", 16, true, true, true, true, true),
    ALPHANUMERIC("Alphanumeric", "Letters & digits only (no special symbols)", 16, true, true, true, false, false),
    PIN("PIN Code", "Numeric only for device or SIM PINs", 6, false, false, true, false, false)
}

data class GeneratedPasswordInfo(
    val password: String,
    val length: Int,
    val entropyBits: Double,
    val poolSize: Int,
    val crackTimeEstimate: String,
    val uppercaseCount: Int,
    val lowercaseCount: Int,
    val digitsCount: Int,
    val symbolsCount: Int
)

class PasswordGenerator {

    private val secureRandom = SecureRandom()

    private val allUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val allLower = "abcdefghijklmnopqrstuvwxyz"
    private val allDigits = "0123456789"
    private val allSymbols = "!@#$%^&*()-_=+[]{}|;:,.<>?"

    // Ambiguous lookalike characters to exclude when requested
    private val ambiguousChars = setOf('0', 'O', 'o', '1', 'l', 'I', '|', ';', ':', '.')

    fun generate(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
        excludeAmbiguous: Boolean = false
    ): String {
        val targetLength = length.coerceIn(4, 64)
        val pools = mutableListOf<String>()
        val guaranteed = mutableListOf<Char>()

        fun filterPool(source: String): String {
            return if (excludeAmbiguous) {
                source.filterNot { it in ambiguousChars }
            } else {
                source
            }
        }

        if (includeUpper) {
            val pool = filterPool(allUpper)
            if (pool.isNotEmpty()) {
                pools.add(pool)
                guaranteed.add(pool[secureRandom.nextInt(pool.length)])
            }
        }
        if (includeLower) {
            val pool = filterPool(allLower)
            if (pool.isNotEmpty()) {
                pools.add(pool)
                guaranteed.add(pool[secureRandom.nextInt(pool.length)])
            }
        }
        if (includeDigits) {
            val pool = filterPool(allDigits)
            if (pool.isNotEmpty()) {
                pools.add(pool)
                guaranteed.add(pool[secureRandom.nextInt(pool.length)])
            }
        }
        if (includeSymbols) {
            val pool = filterPool(allSymbols)
            if (pool.isNotEmpty()) {
                pools.add(pool)
                guaranteed.add(pool[secureRandom.nextInt(pool.length)])
            }
        }

        // Fallback if all options were deselected or emptied
        if (pools.isEmpty()) {
            val fallback = filterPool(allLower)
            pools.add(fallback)
            guaranteed.add(fallback[secureRandom.nextInt(fallback.length)])
        }

        val allChars = pools.joinToString("")
        val result = ArrayList<Char>(guaranteed)

        while (result.size < targetLength) {
            val randomChar = allChars[secureRandom.nextInt(allChars.length)]
            result.add(randomChar)
        }

        // Cryptographically secure Fisher-Yates shuffle
        for (i in result.indices.reversed()) {
            val j = secureRandom.nextInt(i + 1)
            val temp = result[i]
            result[i] = result[j]
            result[j] = temp
        }

        return result.take(targetLength).joinToString("")
    }

    fun generateDetailedInfo(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
        excludeAmbiguous: Boolean = false
    ): GeneratedPasswordInfo {
        val password = generate(
            length = length,
            includeUpper = includeUpper,
            includeLower = includeLower,
            includeDigits = includeDigits,
            includeSymbols = includeSymbols,
            excludeAmbiguous = excludeAmbiguous
        )

        var poolSize = 0
        if (includeUpper) poolSize += if (excludeAmbiguous) allUpper.count { it !in ambiguousChars } else allUpper.length
        if (includeLower) poolSize += if (excludeAmbiguous) allLower.count { it !in ambiguousChars } else allLower.length
        if (includeDigits) poolSize += if (excludeAmbiguous) allDigits.count { it !in ambiguousChars } else allDigits.length
        if (includeSymbols) poolSize += if (excludeAmbiguous) allSymbols.count { it !in ambiguousChars } else allSymbols.length
        if (poolSize == 0) poolSize = 26

        val entropy = length * log2(poolSize.toDouble())

        val crackTime = when {
            entropy < 28 -> "Instant (< 1 second)"
            entropy < 36 -> "A few seconds"
            entropy < 52 -> "Several days"
            entropy < 64 -> "Several centuries"
            entropy < 80 -> "Millions of years"
            else -> "Trillions of years"
        }

        return GeneratedPasswordInfo(
            password = password,
            length = password.length,
            entropyBits = entropy,
            poolSize = poolSize,
            crackTimeEstimate = crackTime,
            uppercaseCount = password.count { it.isUpperCase() },
            lowercaseCount = password.count { it.isLowerCase() },
            digitsCount = password.count { it.isDigit() },
            symbolsCount = password.count { !it.isLetterOrDigit() }
        )
    }
}
