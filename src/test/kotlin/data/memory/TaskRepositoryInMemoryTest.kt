package com.berlin.data.memory

import com.berlin.domain.exception.TaskAlreadyExistsException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.google.common.truth.Truth.assertThat
import org.berlin.data.DummyData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryInMemoryTest {

    private lateinit var repo: TaskRepositoryInMemory

    private val alice = User("U1", "alice", "pw", UserRole.MATE)
    private val bob = User("U2", "bob", "pw", UserRole.MATE)

    @BeforeEach
    fun setUp() {/*  Clean the shared list before every test  */
        DummyData.tasks.clear()
        repo = TaskRepositoryInMemory()
    }

    /* ------------------- create ------------------- */

    @Test
    fun `create succeeds for new id`() {
        val result = repo.create(task("1"))
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `create fails for duplicate id`() {
        repo.create(task("1"))
        val result = repo.create(task("1"))
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskAlreadyExistsException::class.java)
    }

    /* ------------------- findById ------------------- */

    @Test
    fun `findById returns task when present`() {
        val t = task("1")
        repo.create(t)
        val result = repo.findById("1")
        assertThat(result.getOrNull()).isEqualTo(t)
    }

    @Test
    fun `findById fails when absent`() {
        val result = repo.findById("42")
        assertThat(result.isFailure).isTrue()
    }

    /* ------------------- update ------------------- */

    @Test
    fun `update succeeds for existing task`() {
        val original = task("1")
        repo.create(original)
        val changed = original.copy(title = "New title")
        val result = repo.update(changed)
        assertThat(result.getOrNull()).isEqualTo(changed)
    }

    @Test
    fun `update fails when task not found`() {
        val result = repo.update(task("1"))
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
    }

    /* ------------------- findTasksByProjectId ------------------- */

    @Test
    fun `findTasksByProjectId returns matching list`() {
        val t1 = task("1", projectId = "P1")
        val t2 = task("2", projectId = "P1")
        val t3 = task("3", projectId = "P2")
        repo.create(t1); repo.create(t2); repo.create(t3)

        val result = repo.findTasksByProjectId("P1")
        assertThat(result.getOrNull()).containsExactly(t1, t2)
    }

    /* ------------------- delete ------------------- */

    @Test
    fun `delete succeeds when task exists`() {
        repo.create(task("1"))
        val result = repo.delete("1")
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `delete fails when task does not exist`() {
        val result = repo.delete("46")
        assertThat(result.exceptionOrNull()).isInstanceOf(TaskNotFoundException::class.java)
    }

    /* ------------------- nextId ------------------- */

    @Test
    fun `nextId increments with size`() {
        repo.create(task("1"))
        repo.create(task("2"))
        val next = repo.nextId()
        assertThat(next).isEqualTo("3")
    }

    /* ------------ helper to build Task ------------ */

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
}
