package com.berlin.domain.hashAlgorithm

import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.hashPassword.MD5Hasher
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MD5HashAlgorithmTest {

    private lateinit var mD5HashAlgorithm: HashingString

    @BeforeEach
    fun setup() {
        mD5HashAlgorithm = MD5Hasher()
    }

    @Test
    fun `hashPassword should return expected MD5 hash when password is password123`() {
        // Given
        val password = "password123"
        val expectedHash = "482c811da5d5b4bc6d497ffa98491e38"

        // When
        val result = mD5HashAlgorithm.hashPassword(password)

        // Then
        assertThat(result).isEqualTo(expectedHash)
    }

    @Test
    fun `hashPassword should return same hash when input same password`() {
        // Given
        val password = "password123"

        // When
        val resultInputOne = mD5HashAlgorithm.hashPassword(password)
        val resultInputTwo = mD5HashAlgorithm.hashPassword(password)

        // Then
        assertThat(resultInputOne).isEqualTo(resultInputTwo)
    }
}
