package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import com.google.common.truth.Truth.assertThat
import data.UserCache
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class DeleteProjectUseCaseTest {

    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private val addAuditLogUseCase: AddAuditLogUseCase = mockk(relaxed = true)
    private val cashedUser: UserCache = mockk(relaxed = true)
    private val validator: Validator = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteProjectUseCase = DeleteProjectUseCase(
            projectRepository, addAuditLogUseCase, cashedUser,validator
        )
    }

    @Test
    fun `should return Deleted Successfully when project deleted successfully`() {
        // Given
        every { validator.isValid(any()) } returns true
        every { projectRepository.deleteProject(any()) } returns "Deleted Successfully"
        every { cashedUser.currentUser.id } returns "user_123"

        // When
        val result = deleteProjectUseCase("project_1")

        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
        verify(exactly = 1) {
            addAuditLogUseCase(
                createdByUserId = "user_123", auditAction = any(), entityType = any(), entityId = any()
            )
        }
    }

    @Test
    fun `should return throw ProjectNotFoundException when project deletion fails`() {
        // Given
        every { validator.isValid("P1") } returns true
        every { projectRepository.deleteProject("P1") } throws InvalidProjectException("")

        // When// Then
        assertThrows<InvalidProjectException> {
            deleteProjectUseCase("P1")
        }


    }

    @Test
    fun `should throw exception when project id does not exists`() {
        // Given
        every { validator.isValid(any()) } returns true
        every { projectRepository.deleteProject(any()) } throws InvalidProjectException("")

        // When// Then
        assertThrows<InvalidProjectException> {
            deleteProjectUseCase("P2")
        }


    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project ID is invalid`(projectId: String) {
        // When && Then
        assertThrows<Exception> {
            deleteProjectUseCase(projectId)
        }
    }

}