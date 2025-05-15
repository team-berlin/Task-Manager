package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.validation.Validator
import com.berlin.helper.projectHelper
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

class UpdateProjectUseCaseTest {

    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private val addAuditLogUseCase: AddAuditLogUseCase = mockk(relaxed = true)
    private val cashedUser: UserCache = mockk(relaxed = true)
    private val validator: Validator = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        updateProjectUseCase = UpdateProjectUseCase(
            projectRepository, addAuditLogUseCase, cashedUser,validator
        )
    }

    @Test
    fun `should return Updated Successfully when project update succeeds`() {
        // Given
        val project = projectHelper()
        every { validator.isValid(any()) }returns true
        every { projectRepository.updateProject(project) } returns "Updated Successfully"
        every { cashedUser.currentUser.id } returns "user_123"

        // When
        val result = updateProjectUseCase(project)

        // Then
        assertThat(result).isEqualTo("Updated Successfully")
        verify(exactly = 1) {
            addAuditLogUseCase(
                createdByUserId = "user_123", auditAction = any(), entityType = any(), entityId = any()
            )
        }
    }

    @Test
    fun `should return throw InvalidProjectException when project update fails`() {
        // Given
        val project = projectHelper()
        every { projectRepository.updateProject(project) } throws InvalidProjectException("")

        // When // Then
        assertThrows<InvalidProjectException> {
            updateProjectUseCase(project)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project ID is invalid`(
        invalidName: String
    ) {
        // When && Then
        assertThrows<Exception> {
            updateProjectUseCase(
                projectHelper(
                    name = invalidName
                )
            )
        }
    }
}