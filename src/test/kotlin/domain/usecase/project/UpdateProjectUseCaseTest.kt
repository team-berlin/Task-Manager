package com.berlin.domain.usecase.project;

import com.berlin.helper.projectHelper
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
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

    @BeforeEach
    fun setup() {
        updateProjectUseCase = UpdateProjectUseCase(projectRepository,
            addAuditLogUseCase, cashedUser)
    }

    @Test
    fun `should return success when project update succeeds`() {
        // Given
        val project = projectHelper()
        every { projectRepository.updateProject(project) } returns Result.success("Updated Successfully")
        every { cashedUser.currentUser.id } returns "user_123"

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        assertThat(result).isEqualTo(Result.success("Updated Successfully"))
        verify(exactly = 1) {
            addAuditLogUseCase.addAuditLog(
                createdByUserId = "user_123", auditAction = any(), entityType = any(), entityId = any()
            )
        }
    }

    @Test
    fun `should return failure when project update fails`() {
        // Given
        val project = projectHelper()
        every { projectRepository.updateProject(project) } returns Result.failure(Exception())

        // When
        val result = updateProjectUseCase.updateProject(project)

        // Then
        result.onFailure { exception ->
            assertThat(exception.message).isEqualTo("Update Failed")
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `should throw exception when project ID is invalid`(
        invalidName: String
    ) {
        // When && Then
        assertThrows<Exception> {
            updateProjectUseCase.updateProject(
                projectHelper(
                    name = invalidName
                )
            )
        }
    }
}