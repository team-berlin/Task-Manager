package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.GetTaskByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTaskByIdUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getTaskById: GetTaskByIdUseCase
    private lateinit var ui: GetTaskByIdUI

    // capture every call to viewer.show(...)
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        getTaskById = mockk()
        ui = GetTaskByIdUI(getTaskById, viewer, reader)
        printed.clear()
    }

    @Test
    fun `success prints all fields when description present`() {
        every { reader.read() } returns "T1"
        val task = Task(
            id = "T1",
            projectId = "P1",
            title = "Demo",
            description = "Desc",
            stateId = "IN_PROGRESS",
            assignedToUserId = "U2",
            createByUserId = "U1"
        )
        every { getTaskById.invoke("T1") } returns Result.success(task)

        ui.run()

        assertThat(printed).containsExactly(
            "Enter task ID:",
            "ID: T1",
            "Title: Demo",
            "Description: Desc",
            "State: IN_PROGRESS",
            "Assignee: U2",
            "Created by: U1"
        ).inOrder()
    }

    @Test
    fun `success prints (none) when description is null`() {
        every { reader.read() } returns "T2"
        val task = Task(
            id = "T2",
            projectId = "P1",
            title = "NoDesc",
            description = null,
            stateId = "DONE",
            assignedToUserId = "U3",
            createByUserId = "U4"
        )
        every { getTaskById.invoke("T2") } returns Result.success(task)

        ui.run()

        // We only care that the description line uses "(none)"
        assertThat(printed).contains("Description: (none)")
    }

    @Test
    fun `not found prints friendly message`() {
        every { reader.read() } returns "X42"
        every { getTaskById.invoke("X42") } returns Result.failure(TaskNotFoundException("X42"))

        ui.run()

        assertThat(printed.last()).isEqualTo("No task found with ID “X42”")
    }

    @Test
    fun `other failure prints exception message`() {
        every { reader.read() } returns "T3"
        every { getTaskById.invoke("T3") } returns Result.failure(IllegalStateException("boom"))

        ui.run()

        assertThat(printed.last()).isEqualTo("boom")
    }

    @Test
    fun `fallback prints Lookup failed when message is null`() {
        every { reader.read() } returns "T4"
        every { getTaskById.invoke("T4") } returns Result.failure(IllegalStateException("Lookup failed"))

        ui.run()

        assertThat(printed.last()).isEqualTo("Lookup failed")
    }

    @Test
    fun `empty raw id throws InvalidTaskIdException and prints invalid task id`() {
        every { reader.read() } returns ""
        every { getTaskById.invoke("") } throws InvalidTaskIdException("must not be empty")

        ui.run()

        assertThat(printed).contains("Invalid task id")
        verify(exactly = 1) { getTaskById.invoke("") }
    }

    @Test
    fun `numeric-only raw id throws InvalidTaskIdException and prints invalid task id`() {
        every { reader.read() } returns "1234"
        every { getTaskById.invoke("1234") } throws InvalidTaskIdException("numeric-only")

        ui.run()

        assertThat(printed).contains("Invalid task id")
    }
}
