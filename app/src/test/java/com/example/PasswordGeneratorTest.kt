package com.example

import com.example.domain.security.PasswordGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PasswordGeneratorTest {

    private lateinit var generator: PasswordGenerator

    @Before
    fun setUp() {
        generator = PasswordGenerator()
    }

    @Test
    fun testGenerateDefaultLength() {
        val pass = generator.generate(length = 16)
        assertEquals(16, pass.length)
    }

    @Test
    fun testGenerateCustomLengths() {
        val pass8 = generator.generate(length = 8)
        assertEquals(8, pass8.length)

        val pass32 = generator.generate(length = 32)
        assertEquals(32, pass32.length)
    }

    @Test
    fun testInclusionsGuaranteed() {
        val pass = generator.generate(
            length = 24,
            includeUpper = true,
            includeLower = true,
            includeDigits = true,
            includeSymbols = true
        )

        assertTrue(pass.any { it.isUpperCase() })
        assertTrue(pass.any { it.isLowerCase() })
        assertTrue(pass.any { it.isDigit() })
        assertTrue(pass.any { !it.isLetterOrDigit() })
    }

    @Test
    fun testDigitsOnly() {
        val pass = generator.generate(
            length = 12,
            includeUpper = false,
            includeLower = false,
            includeDigits = true,
            includeSymbols = false
        )
        assertEquals(12, pass.length)
        assertTrue(pass.all { it.isDigit() })
    }

    @Test
    fun testRandomness() {
        val pass1 = generator.generate(length = 16)
        val pass2 = generator.generate(length = 16)
        assertNotEquals(pass1, pass2)
    }

    @Test
    fun testExcludeAmbiguousCharacters() {
        val ambiguous = setOf('0', 'O', 'o', '1', 'l', 'I', '|', ';', ':', '.')
        for (i in 0 until 10) {
            val pass = generator.generate(length = 32, excludeAmbiguous = true)
            assertTrue(pass.none { it in ambiguous })
        }
    }

    @Test
    fun testDetailedInfoGeneration() {
        val info = generator.generateDetailedInfo(length = 20)
        assertEquals(20, info.length)
        assertTrue(info.entropyBits > 60.0)
        assertEquals(20, info.uppercaseCount + info.lowercaseCount + info.digitsCount + info.symbolsCount)
        assertTrue(info.password.isNotEmpty())
    }
}
