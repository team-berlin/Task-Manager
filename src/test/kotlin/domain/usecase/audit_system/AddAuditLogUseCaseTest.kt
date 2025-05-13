package com.berlin.domain.usecase.audit_system

import com.berlin.domain.exception.InvalidAuditLogException
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.model.*
import com.berlin.domain.repository.AuditRepository
import com.berlin.helper.generateAuditLog
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class AddAuditLogUseCaseTest {
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val idGenerator: IdGenerator = mockk()
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase

    @BeforeEach
    fun setup() {
        addAuditLogUseCase = AddAuditLogUseCase(auditRepository, idGenerator)

    }

    @Test
    fun `should audit log is added successfully with no changesDescription`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId, changesDescription = null)
        every { idGenerator.generateId("AUDIT", any(), any()) } returns generatedId

        // When
        addAuditLogUseCase.invoke(
            createdByUserId = auditLog.createdByUserId,
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        verify(exactly = 1) { idGenerator.generateId("AUDIT", any(), any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }

    @Test
    fun `should return success when audit log is added successfully with changesDescription`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId, changesDescription = "update")
        every { idGenerator.generateId("AUDIT", any(), any()) } returns generatedId

        // When
        addAuditLogUseCase.invoke(
            createdByUserId = auditLog.createdByUserId,
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        verify(exactly = 1) { idGenerator.generateId("AUDIT", any(), any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }

    @Test
    fun `should throw exception when audit log failed to add`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId)
        every { idGenerator.generateId("AUDIT", any(), any()) } returns generatedId
        every { auditRepository.addAuditLog(any()) } throws InvalidAuditLogException("")

        // When
        assertThrows<InvalidAuditLogException> {
            addAuditLogUseCase.invoke(
                createdByUserId = auditLog.createdByUserId,
                auditAction = auditLog.auditAction,
                changesDescription = auditLog.changesDescription,
                entityType = auditLog.entityType,
                entityId = auditLog.entityId
            )
        }
    }

    @Test
    fun `should return failure when id generator throws exception`() {
        // Given
        every { idGenerator.generateId("AUDIT", any(), any()) } throws InvalidAuditLogException("")

        // When
        assertThrows<InvalidAuditLogException> {
            addAuditLogUseCase.invoke(
                createdByUserId = "u1",
                auditAction = AuditLog.AuditAction.CREATE,
                changesDescription = "Created something",
                entityType = AuditLog.EntityType.TASK,
                entityId = "G2"
            )
        }
    }
}
