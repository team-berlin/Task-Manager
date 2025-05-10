package com.berlin.domain.usecase.task

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AssignTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var useCase: AssignTaskUseCase

    private val creator = User("U0", "alice", "pw",  UserRole.ADMIN)
    private val oldAssignee = User("U1", "john", "pw",  UserRole.MATE)
    private val anotherAssignee = User("U2", "bob", "pw", UserRole.MATE)

    private val stored = Task(
        id = "1",
        projectId = "P1",
        title = "Demo",
        description = null,
        stateId = "TODO",
        assignedToUserId = oldAssignee.id,
        createByUserId = creator.id
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        useCase = AssignTaskUseCase(taskRepository)
    }


    @Test
    fun `result is success when assignee changes`() = runTest {
        stubHappyPath()
        val result = useCase("1", anotherAssignee.id)
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `repository update is called with new assignee`() = runTest {
        stubHappyPath()
        useCase("1", anotherAssignee.id)

        coVerify(exactly = 1) {
            taskRepository.update(
                match { it.id == "1" && it.assignedToUserId == anotherAssignee.id })
        }
    }


    @Test
    fun `result is failure when task is not found`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.failure(TaskNotFoundException("1"))

        val result = useCase("1", anotherAssignee.id)
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `result is failure when repository update returns unexpected error`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(stored)
        coEvery { taskRepository.update(any()) } returns Result.failure(IllegalStateException("boom"))

        val result = useCase("1", anotherAssignee.id)
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `throws InvalidAssigneeException when assignee id is blank`() = runTest {
        coEvery { taskRepository.findById("1") } returns Result.success(stored)

        assertThrows<InvalidAssigneeException> {
            useCase("1", "   ")
        }

        coVerify(exactly = 0) { taskRepository.update(any()) }
    }

    private fun stubHappyPath() {
        coEvery { taskRepository.findById("1") } returns Result.success(stored)
        coEvery { taskRepository.update(any()) } answers { Result.success(firstArg()) }
    }
}
