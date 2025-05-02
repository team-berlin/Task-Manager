package logic.usecase.auditSystem

import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.EntityType
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.helper.generateAuditLog
import com.berlin.logic.generateIdHelper.IdGenerator
import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.usecase.auditSystem.AddAuditLogUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        every { idGenerator.generateId("AUDIT",any(),any()) } returns generatedId
        every { auditRepository.addAuditLog(any()) } returns Result.success("Audit log added successfully")

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdBy = User("u1", "TestUser", "ffkjkuyu", UserRole.ADMIN),
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription!!,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("Audit log added successfully")

        verify(exactly = 1) { idGenerator.generateId("AUDIT",any(),any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }

    @Test
    fun `should return failure when audit log failed to add`() {
        // Given
        val generatedId = "AUDIT_12345"
        val auditLog = generateAuditLog(id = generatedId)
        every { idGenerator.generateId("AUDIT",any(),any()) } returns generatedId
        every { auditRepository.addAuditLog(any()) } returns Result.failure(Exception("DB error"))

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdBy = User("u1", "TestUser", "ffkjkuyu", UserRole.ADMIN),
            auditAction = auditLog.auditAction,
            changesDescription = auditLog.changesDescription!!,
            entityType = auditLog.entityType,
            entityId = auditLog.entityId
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Audit log failed to add")

        verify(exactly = 1) { idGenerator.generateId("AUDIT",any(),any()) }
        verify(exactly = 1) { auditRepository.addAuditLog(match { it.id == generatedId }) }
    }

    @Test
    fun `should return failure when id generator throws exception`() {
        // Given
        every { idGenerator.generateId("AUDIT",any(),any()) } throws IllegalArgumentException("Invalid prefix")

        // When
        val result = addAuditLogUseCase.addAuditLog(
            createdBy = User("u1", "TestUser", "ffkjkuyu", UserRole.ADMIN),
            auditAction = AuditAction.CREATE,
            changesDescription = "Created something",
            entityType = EntityType.TASK,
            entityId = "G2"
        )

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Audit log failed to add")

        verify(exactly = 1) { idGenerator.generateId("AUDIT",any(),any()) }
        verify(exactly = 0) { auditRepository.addAuditLog(any()) }
    }
}