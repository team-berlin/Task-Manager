package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.model.*
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.helper.generateAuditLog
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AddAuditLogUseCaseTest {
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val idGenerator: IdGenerator = mockk()
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase

    @BeforeEach
    fun setup() {
        addAuditLogUseCase = AddAuditLogUseCase(auditRepository, idGenerator)

    }

    @Test
    fun `should return success when audit log is added successfully`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId)
        every { idGenerator.generateId("AUDIT", any(), any()) } returns generatedId
        every { auditRepository.addAuditLog(any()) } returns Result.success("Audit log added successfully")

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdByUserId = auditLog.createdByUserId,
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription!!,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("Audit log added successfully")

        verify(exactly = 1) { idGenerator.generateId("AUDIT", any(), any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }

    @Test
    fun `should return failure when audit log failed to add`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId)
        every { idGenerator.generateId("AUDIT", any(), any()) } returns generatedId
        every { auditRepository.addAuditLog(any()) } returns Result.failure(Exception("audit log failed to add"))

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdByUserId = auditLog.createdByUserId,
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription!!,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Audit log failed to add")

        verify(exactly = 1) { idGenerator.generateId("AUDIT", any(), any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }


    @Test
    fun `should return failure when id generator throws exception`() {
        // Given
        every { idGenerator.generateId("AUDIT", any(), any()) } throws IllegalArgumentException("Invalid prefix")

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdByUserId = "u1",
            auditAction = AuditAction.CREATE,
            changesDescription = "Created something",
            entityType = EntityType.TASK,
            entityId = "G2"
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Audit log failed to add")

        verify(exactly = 1) { idGenerator.generateId("AUDIT", any(), any()) }
        verify(exactly = 0) { auditRepository.addAuditLog(any()) }
    }


}