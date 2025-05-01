package logic.usecase.auditSystem

import com.berlin.helper.generateAuditLog
import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.usecase.auditSystem.GetAuditLogsByTaskIdUseCase
import com.berlin.model.EntityType
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetAuditLogsByTaskIdUseCaseTest {
    private val auditRepository: AuditRepository = mockk(relaxed = true)
    private lateinit var getAuditLogsByTaskIdUseCase: GetAuditLogsByTaskIdUseCase

    @BeforeEach
    fun setup() {
        getAuditLogsByTaskIdUseCase = GetAuditLogsByTaskIdUseCase(auditRepository)
    }

    @Test
    fun `should return list of logs for existing task ID`() {
        // Given
        val taskId = "T123"
        val logs = listOf(
            generateAuditLog(id = "A3", entityId = taskId, entityType = EntityType.TASK)
        )
        every { auditRepository.getAuditLogsByProjectId(taskId) } returns logs

        //When
        val result = getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId(taskId)

        //That
        assertThat(result).isEqualTo(logs)
        verify(exactly = 1) { auditRepository.getAuditLogsByProjectId(taskId) }

    }


    @ParameterizedTest
    @ValueSource(strings = ["", "   ","123"])
    fun `should throw Exception when task id is invalid`(invalidId: String) {
        //when&then
        assertThrows<IllegalArgumentException> {
            getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId(invalidId)
        }
    }

}