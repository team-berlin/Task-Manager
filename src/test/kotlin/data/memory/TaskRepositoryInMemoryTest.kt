package com.berlin.data.memory

import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryInMemoryTest {

    private lateinit var taskRepository: TaskRepositoryInMemory

    private val alice = User(id = "U1", userName = "alice", password = "pw", role = UserRole.MATE)
    private val bob = User(id = "U2", userName = "bpb", password = "pw", role = UserRole.MATE)

    @BeforeEach
    fun setUp() {
        taskRepository = TaskRepositoryInMemory()
    }


    @Test
    fun `create succeeds for new id`() {
        val result = taskRepository.create(makeTask("1"))
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `create fails for duplicate id`() {
        taskRepository.create(makeTask("1"))
        val result = taskRepository.create(makeTask("1"))
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskAlreadyExistsException::class.java)
    }

    @Test
    fun `findById returns task when present`() {
        val task = makeTask("1")
        taskRepository.create(task)
        val result = taskRepository.findById("1")
        assertThat(result.getOrNull()).isEqualTo(task)
    }

    @Test
    fun `findById fails when absent`() {
        val result = taskRepository.findById("42")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `update succeeds for existing task`() {
        val original = makeTask("1")
        taskRepository.create(original)
        val changed = original.copy(title = "New title")
        val result = taskRepository.update(changed)
        assertThat(result.getOrNull()).isEqualTo(changed)
    }

    @Test
    fun `update fails when task not found`() {
        val result = taskRepository.update(makeTask("1"))
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
    }

    @Test
    fun `findTasksByProjectId returns matching list`() {
        val t1 = makeTask("1", projectId = "P1")
        val t2 = makeTask("2", projectId = "P1")
        val t3 = makeTask("3", projectId = "P2")
        taskRepository.create(t1); taskRepository.create(t2); taskRepository.create(t3)

        val result = taskRepository.findTasksByProjectId("P1")

        assertThat(result.getOrNull()).containsExactly(t1, t2)
    }

    @Test
    fun `delete succeeds when task exists`() {
        taskRepository.create(makeTask("1"))
        val result = taskRepository.delete("1")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `delete fails when task does not exist`() {
        val result = taskRepository.delete("46")
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
    }

    @Test
    fun `nextId increments with size`() {
        taskRepository.create(makeTask("1"))
        taskRepository.create(makeTask("2"))
        val next = taskRepository.nextId()
        assertThat(next).isEqualTo("3")
    }

    private fun makeTask(
        id: String,
        projectId: String = "P1",
        title: String = "Title $id",
    ) = Task(
        id = id,
        projectId = projectId,
        title = title,
        description = null,
        stateId = "TODO",
        assignedTo = bob,
        createBy = alice,
        auditLogs = emptyList()
    )
}
