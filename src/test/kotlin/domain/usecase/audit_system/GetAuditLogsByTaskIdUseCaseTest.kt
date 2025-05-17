package com.berlin.domain.usecase.auditSystem

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.model.AuditLog
import com.berlin.helper.generateAuditLog
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.usecase.audit_system.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetAuditLogsByTaskIdUseCaseTest {
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private val validator: Validator = mockk(relaxed = true)
    private lateinit var getAuditLogsByTaskIdUseCase: GetAuditLogsByTaskIdUseCase

    @BeforeEach
    fun setup() {
        getAuditLogsByTaskIdUseCase = GetAuditLogsByTaskIdUseCase(auditRepository,validator)
    }

    @Test
    fun `should return list of logs for existing task ID`() {
        // Given
        val taskId = "T123"
        val logs = listOf(
            generateAuditLog(id = "A3", entityId = taskId, entityType = AuditLog.EntityType.TASK)
        )
        every { validator.isValid(taskId) } returns true
        every { auditRepository.getAuditLogsByTaskId(taskId) } returns logs

        //When
        val result = getAuditLogsByTaskIdUseCase(taskId)

        //That
        assertThat(result).isEqualTo(logs)
    }


    @ParameterizedTest
    @ValueSource(strings = ["", "   ","123"])
    fun `should throw Exception when task id is invalid`(invalidId: String) {
        //when&then
        assertThrows<InvalidTaskIdException> {
            getAuditLogsByTaskIdUseCase(invalidId)
        }
    }

}