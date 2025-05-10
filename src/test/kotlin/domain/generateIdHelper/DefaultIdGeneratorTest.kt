package com.berlin.domain.generateIdHelper

import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class IdGeneratorImplementationTest {

    private lateinit var idGeneratorImplementation:
            IdGeneratorImplementation

    @BeforeEach
    fun setup() {
        idGeneratorImplementation = IdGeneratorImplementation()
    }

    @Test
    fun `should generate ID with correct padding and timestamp`() {
        // Given
        val input = "Test"
        val padChar = 'X'
        val padCharLength = 8

        // When
        val generatedId = idGeneratorImplementation.generateId(input, padChar, padCharLength)

        // Then
        assertThat(generatedId.startsWith("TestXXXX_")).isTrue()
        assertThat(generatedId.substringAfter("_").toIntOrNull()).isNotNull()
    }

    @Test
    fun `should throw exception when input is empty`() {
        // Given
        val emptyInput = " "

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            idGeneratorImplementation.generateId(emptyInput, 'X', 8)
        }
        assertThat(exception.message).isEqualTo("String must not be empty")
    }

    @Test
    fun `should pad input correctly when length is shorter than pad size`() {
        // Given
        val input = "AB"
        val padChar = 'Y'
        val padCharLength = 6

        // When
        val generatedId = idGeneratorImplementation.generateId(input, padChar, padCharLength)

        // Then
        assertThat(generatedId.startsWith("ABYYYY_")).isTrue()
    }

    @Test
    fun `should not modify input if it already matches pad length`() {
        // Given
        val input = "ValidStr"
        val padChar = 'Z'
        val padCharLength = 9 // One extra padding

        // When
        val generatedId = idGeneratorImplementation.generateId(input, padChar, padCharLength)

        // Then
        assertThat(generatedId.startsWith("ValidStrZ_")).isTrue()
    }

}