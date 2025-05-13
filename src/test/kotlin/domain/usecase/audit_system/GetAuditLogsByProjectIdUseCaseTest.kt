package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.model.AuditLog
import com.berlin.helper.generateAuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.audit_system.GetAuditLogsByProjectIdUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetAuditLogsByProjectIdUseCaseTest {

    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private lateinit var getAuditLogsByProjectIdUseCase: GetAuditLogsByProjectIdUseCase

    @BeforeEach
    fun setup() {
        getAuditLogsByProjectIdUseCase = GetAuditLogsByProjectIdUseCase(auditRepository)
    }

    @Test
    fun `should return list of logs for existing project ID`() {
        // Given
        val projectId = "D123"
        val logs = listOf(
            generateAuditLog(id = "A2", entityId = projectId, entityType = AuditLog.EntityType.PROJECT)
        )
        every { auditRepository.getAuditLogsByProjectId(projectId) } returns logs

        //When
        val result = getAuditLogsByProjectIdUseCase.getAuditLogsByProjectId(projectId)

        //That
        assertThat(result).isEqualTo(logs)
        verify(exactly = 1) { auditRepository.getAuditLogsByProjectId(projectId) }

    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "123"])
    fun `should throw exception when project Id is invalid`(invalidId: String) {
        //when&then
        assertThrows<IllegalArgumentException> {
            getAuditLogsByProjectIdUseCase.getAuditLogsByProjectId(invalidId)
        }
    }

}