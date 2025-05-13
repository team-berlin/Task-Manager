package com.berlin.data.task

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskDto
import com.berlin.data.mapper.TaskMapper
import com.berlin.data.repository.TaskRepositoryImpl
import com.berlin.domain.exception.InvalidTaskException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryImplTest {

    private lateinit var repo: TaskRepositoryImpl
    private lateinit var baseDataSource: BaseDataSource<TaskDto>
    private lateinit var taskMapper: TaskMapper

    @BeforeEach
    fun setUp() {
        baseDataSource = mockk(relaxed = true)
        taskMapper = mockk(relaxed = true)
        repo = TaskRepositoryImpl(baseDataSource, taskMapper)
    }

    @Test
    fun `createTask succeeds for new task and return this task`() {
        val task = task("1")
        val taskDto = taskDto("1")
        every { taskMapper.mapToDataModel(task) } returns taskDto
        every { baseDataSource.write(any()) } returns true

        val result = repo.createTask(task)

        assertThat(result).isEqualTo(task)
    }

    @Test
    fun `createTask throw exception when write returns false`() {
        val task = task("X")
        every { baseDataSource.write(any()) } returns false

        assertThrows(InvalidTaskException::class.java) {
            repo.createTask(task)
        }
    }

    @Test
    fun `updateTask succeeds when task found and return task`() {
        val task = task("1")
        every { baseDataSource.update(any(), any()) } returns true

        val result = repo.updateTask(task)

        assertThat(result).isEqualTo(task)
    }

    @Test
    fun `updateTask fails when task not found`() {
        val task = task("1")
        every { baseDataSource.update(any(), any()) } returns false

        assertThrows(InvalidTaskException::class.java) {
            repo.updateTask(task)
        }
    }

    @Test
    fun `getTaskById returns task when present`() {
        val task = task("1")
        val taskDto = taskDto("1")
        every { baseDataSource.getById("1") } returns taskDto
        every { taskMapper.mapToDomainModel(any()) } returns task

        val result = repo.getTaskById("1")

        assertThat(result).isEqualTo(task)
    }

    @Test
    fun `getTaskById throws exception when absent`() {
        every { baseDataSource.getById("42") } returns null

        assertThrows(TaskNotFoundException::class.java) {
            repo.getTaskById("42")
        }
    }

    @Test
    fun `getTasksByProjectId returns list of tasks when project id found`() {
        val task = task("1")
        val taskDto = taskDto("1")

        every { baseDataSource.getAll() } returns listOf(taskDto)

        every { taskMapper.mapToDomainModel(any()) } returns task

        val result = repo.getTasksByProjectId("P1")

        assertThat(result).isEqualTo(listOf(task))
    }

    @Test
    fun `deleteTask succeeds when task exists`() {
        every { baseDataSource.delete("1") } returns true

        repo.deleteTask("1")
    }

    @Test
    fun `deleteTask fails when task does not exist`() {
        every { baseDataSource.delete("46") } returns false

        assertThrows(TaskNotFoundException::class.java) {
            repo.deleteTask("46")
        }
    }

    @Test
    fun `getAllTasks returns list of tasks when project id found`() {
        val task = task("1")
        val taskDto = taskDto("1")

        every { baseDataSource.getAll() } returns listOf(taskDto)

        every { taskMapper.mapToDomainModel(any()) } returns task

        val result = repo.getAllTasks()

        assertThat(result).isEqualTo(listOf(task))
    }

    private val alice = User("U1", "alice", UserRole.MATE)
    private val bob = User("U2", "bob", UserRole.MATE)

    private fun task(
        id: String,
        projectId: String = "P1",
        title: String = "Title $id",
    ) = Task(
        id = id,
        projectId = projectId,
        title = title,
        description = null,
        stateId = "TODO",
        assignedToUserId = bob.id,
        createByUserId = alice.id
    )

    private fun taskDto(
        id: String,
        projectId: String = "P1",
        title: String = "Title $id",
    ) = TaskDto(
        id = id,
        projectId = projectId,
        title = title,
        description = null,
        stateId = "TODO",
        assignedToUserId = bob.id,
        createByUserId = alice.id
    )
}
