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
import org.junit.jupiter.api.assertThrows

class GetTaskByIdUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var getTaskByIdUI: GetTaskByIdUI

    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        getTaskByIdUseCase = mockk()
        getTaskByIdUI = GetTaskByIdUI(getTaskByIdUseCase, viewer, reader)
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
        every { getTaskByIdUseCase.invoke("T1") } returns task

        getTaskByIdUI.run()

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
    fun `success prints '(none)' when description is null`() {
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
        every { getTaskByIdUseCase.invoke("T2") } returns task

        getTaskByIdUI.run()

        assertThat(printed).contains("Description: (none)")
    }

    @Test
    fun `not found throws TaskNotFoundException`() {
        every { reader.read() } returns "X42"
        every { getTaskByIdUseCase.invoke("X42") } throws TaskNotFoundException("X42")

        assertThrows<TaskNotFoundException> { getTaskByIdUI.run() }
    }

    @Test
    fun `other failure propagates exception`() {
        every { reader.read() } returns "T3"
        every { getTaskByIdUseCase.invoke("T3") } throws IllegalStateException("boom")

        assertThrows<IllegalStateException> { getTaskByIdUI.run() }
    }

    @Test
    fun `empty raw id throws and prints invalid task id`() {
        every { reader.read() } returns ""
        every { getTaskByIdUseCase.invoke("") } throws InvalidTaskIdException("must not be empty")

        getTaskByIdUI.run()

        assertThat(printed).contains("Invalid task id")
        verify(exactly = 1) { getTaskByIdUseCase.invoke("") }
    }

    @Test
    fun `numeric-only raw id throws and prints invalid task id`() {
        every { reader.read() } returns "1234"
        every { getTaskByIdUseCase.invoke("1234") } throws InvalidTaskIdException("numeric-only")

        getTaskByIdUI.run()

        assertThat(printed).contains("Invalid task id")
    }
}
