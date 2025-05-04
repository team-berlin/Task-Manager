package com.berlin.presentation.task

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidProjectIdException
import com.berlin.domain.model.*
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.domain.model.Permission
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTasksByProjectIdUITest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private lateinit var useCase: GetTasksByProjectUseCase
    private lateinit var ui: GetTasksByProjectIdUI

    private val projectP1 = Project("P1", "Core", null, listOf("S1"), emptyList())
    private val stateTodo = State("S1", "TODO", "P1")
    private val alice = User("U1", "alice", "pw", permission = Permission(), UserRole.MATE)

    @BeforeEach
    fun setUp() {
        DummyData.projects.clear()
        DummyData.states.clear()
        DummyData.tasks.clear()
        printed.clear()

        DummyData.projects += projectP1
        DummyData.states += stateTodo

        useCase = mockk()
        ui = GetTasksByProjectIdUI(useCase, viewer, reader)
    }

    @Test
    fun `shows swimlane with one task`() {
        val task = Task(
            id = "T1",
            projectId = "P1",
            title = "Feature",
            description = null,
            stateId = "S1",
            assignedToUserId = alice.id,
            createByUserId = alice.id
        )
        every { reader.read() } returns "1"
        every { useCase.invoke("P1") } returns Result.success(listOf(task))

        ui.run()

        assertThat(printed).contains("\n=== Tasks for project P1 ===")
        assertThat(printed).contains("- T1: Feature  → ${alice.id}")
    }

    @Test
    fun `prints no states message`() {
        DummyData.states.clear()
        every { reader.read() } returns "1"
        every { useCase.invoke("P1") } returns Result.success(emptyList())

        ui.run()

        assertThat(printed.last()).contains("No states found")
    }

    @Test
    fun `state with zero tasks prints placeholder`() {
        every { reader.read() } returns "1"
        every { useCase.invoke("P1") } returns Result.success(emptyList())

        ui.run()

        assertThat(printed).contains("  (no tasks)")
    }

    @Test
    fun `user cancellation prints Cancelled`() {
        every { reader.read() } returns "X"

        ui.run()

        assertThat(printed.last()).contains("Cancelled.")
        verify(exactly = 0) { useCase.invoke(any()) }
    }

    @Test
    fun `invalid choice prints error message`() {
        every { reader.read() } returns "99"

        ui.run()

        assertThat(printed.last()).contains("Invalid selection")
        verify(exactly = 0) { useCase.invoke(any()) }
    }

    @Test
    fun `onFailure from use case is shown to the user`() {
        every { reader.read() } returns "1"
        every { useCase.invoke("P1") } returns Result.failure(RuntimeException("boom"))

        ui.run()

        assertThat(printed.last()).contains("boom")
        verify(exactly = 1) { useCase.invoke("P1") }
    }

    @Test
    fun `throws InvalidProjectIdException and shows invalid project id`() {
        every { reader.read() } returns "1"
        every { useCase.invoke("P1") } throws InvalidProjectIdException("bad id")

        ui.run()

        assertThat(printed.last()).contains("invalid project id")
        verify(exactly = 1) { useCase.invoke("P1") }
    }
}
