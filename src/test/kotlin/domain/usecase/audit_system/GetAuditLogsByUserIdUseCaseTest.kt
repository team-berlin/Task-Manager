package com.berlin.domain.usecase.auditSystem

import com.berlin.helper.generateAuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.audit_system.GetAuditLogsByUserIdUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetAuditLogsByUserIdUseCaseTest {
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private lateinit var getAuditLogsByUserIdUseCase: GetAuditLogsByUserIdUseCase

    @BeforeEach
    fun setup() {
        getAuditLogsByUserIdUseCase = GetAuditLogsByUserIdUseCase(auditRepository)
    }

    @Test
    fun `should return list of logs for user ID`() {
        // Given
        val userId = "u1"
        val logs = listOf(
            generateAuditLog(createdBy = generateAuditLog().createdByUserId)
        )
        every { auditRepository.getAuditLogsByUserId(userId) } returns logs

        //When
        val result = getAuditLogsByUserIdUseCase.getAuditLogsByUserId(userId)

        //That
        assertThat(result).isEqualTo(logs)
        verify(exactly = 1) { auditRepository.getAuditLogsByUserId(userId) }

    }

    @Test
    fun `should return empty list when no audit logs found for user ID`() {
        // Given
        val userId = "invalid"
        every { auditRepository.getAuditLogsByUserId(userId) } returns emptyList()

        //When
        val result = getAuditLogsByUserIdUseCase.getAuditLogsByUserId(userId)

        //That
        assertThat(result).isEmpty()
        verify(exactly = 1) { auditRepository.getAuditLogsByUserId(userId) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ","123"])
    fun `should throw exception when user id is invalid`(invalidId: String) {
       //when&then
        assertThrows<IllegalArgumentException> {
            getAuditLogsByUserIdUseCase.getAuditLogsByUserId(invalidId)
        }
    }


}