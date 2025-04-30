package com.berlin.domain.usecase.task

import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: AssignTaskUseCase

    private val creator = mockk<User>(relaxed = true)
    private val oldAssignee = mockk<User>(relaxed = true)
    private val newAssignee = mockk<User>(relaxed = true)

    private val stored = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedTo = oldAssignee,
        createBy = creator,
        auditLogs = emptyList()
    )

    @BeforeEach
    fun setUp() {
        taskRepository= mockk()
        useCase = AssignTaskUseCase(taskRepository)
    }

    @Test
    fun `result is success when assignee changes`() {
        stubHappyPath()
        val result = useCase("1", newAssignee)
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `repository update is called with new assignee`() {
        stubHappyPath()
        useCase("1", newAssignee)
        verify(exactly = 1) {
            taskRepository.update(match { it.id == "1" && it.assignedTo == newAssignee })
        }
    }

    @Test
    fun `result is failure when task is not found`() {
        every { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))
        val result = useCase("1", newAssignee)
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `result is failure when repository update returns unexpected error`() {
        every { taskRepository.findById("1") } returns Result.success(stored)
        every { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))
        val result = useCase("1", newAssignee)
        assertThat(result.isFailure).isTrue()
    }

    private fun stubHappyPath() {
        every { taskRepository.findById("1") } returns Result.success(stored)
        every { taskRepository.update(any()) } answers { Result.success(firstArg()) }
    }
}
