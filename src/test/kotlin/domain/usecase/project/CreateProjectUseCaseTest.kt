package com.berlin.domain.usecase.project

import com.berlin.domain.exception.InvalidProjectException
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
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

class CreateProjectUseCaseTest {

    private lateinit var createProjectUseCase: CreateProjectUseCase
    private val projectRepository: ProjectRepository = mockk(relaxed = true)
    private val addAuditLogUseCase: AddAuditLogUseCase = mockk(relaxed = true)
    private val cashedUser: UserCache = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        val idGenerator: IdGenerator = mockk(relaxed = true)
        createProjectUseCase = CreateProjectUseCase(projectRepository, idGenerator, addAuditLogUseCase, cashedUser)
    }

    @Test
    fun `createNewProject should log audit when project is created successfully`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } returns "Creation Successfully"
        every { cashedUser.currentUser.id } returns "user_123"

        // When
        val result = createProjectUseCase(
            validProject.title, validProject.description, validProject.statesId, validProject.tasksId
        )

        // Then
        assertThat(result).isEqualTo("Creation Successfully")
        verify(exactly = 1) {
            addAuditLogUseCase(
                createdByUserId = "user_123", auditAction = any(), entityType = any(), entityId = any()
            )
        }
    }

    @Test
    fun `createNewProject should throw exception when project creation fails`() {
        // Given
        val validProject = projectHelper()
        every { projectRepository.createProject(any()) } throws InvalidProjectException("")
        // When // Then
        assertThrows<Exception> {
            createProjectUseCase(
                validProject.title, validProject.description, validProject.statesId, validProject.tasksId
            )
        }

    }


    @ParameterizedTest
    @ValueSource(strings = ["", " ", "123"])
    fun `validateProjectName should throw exception when project name is invalid`(invalidName: String) {
        // When & Then
        val exception = assertThrows<Exception> {
            createProjectUseCase(invalidName, null, null, null)
        }
        assertThat(exception.message).isEqualTo("Project Name must not be empty or blank")

        // Ensure no repository or audit log calls were made
        verify(exactly = 0) { projectRepository.createProject(any()) }

    }
}