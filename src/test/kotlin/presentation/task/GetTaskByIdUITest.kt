package com.berlin.presentation.task

import com.berlin.domain.exception.InvalidTaskIdException
import com.berlin.domain.exception.TaskNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.task.GetTaskByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTaskByIdUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var useCase: GetTaskByIdUseCase
    private lateinit var ui: GetTaskByIdUI
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            coEvery { show(capture(printed)) } just Runs
        }
        reader = mockk()
        useCase = mockk()
        ui = GetTaskByIdUI(useCase, viewer, reader)
        printed.clear()
    }

    @Test
    fun `success prints all task fields`() = runTest {
        coEvery { reader.read() } returns "T1"
        val task = Task(
            id = "T1",
            projectId = "P1",
            title = "Demo",
            description = "Desc",
            stateId = "IN_PROGRESS",
            assignedToUserId = "U2",
            createByUserId = "U1"
        )
        coEvery { useCase.invoke("T1") } returns Result.success(task)

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
    fun `not found prints friendly message`() = runTest {
        coEvery { reader.read() } returns "X42"
        coEvery { useCase.invoke("X42") } returns Result.failure(TaskNotFoundException("X42"))

        ui.run()

        // no stacktrace, just friendly line
        assertThat(printed.last()).contains("No task found with ID “X42”")
    }

    @Test
    fun `other failure prints exception message`() = runTest {
        coEvery { reader.read() } returns "T2"
        coEvery { useCase.invoke("T2") } returns Result.failure(IllegalStateException("boom"))

        ui.run()

        assertThat(printed.last()).contains("boom")
    }

    @Test
    fun `lookup failed fallback when exception message null`() = runTest {
        coEvery { reader.read() } returns "T3"
        coEvery { useCase.invoke("T3") } returns Result.failure(IllegalStateException("Lookup failed"))

        ui.run()

        assertThat(printed.last()).contains("Lookup failed")
    }

    @Test
    fun `empty raw id triggers InvalidTaskIdException and prints invalid task id`() = runTest {
        coEvery { reader.read() } returns ""
        coEvery { useCase.invoke("") } throws InvalidTaskIdException("Task id must not be empty, blank, or purely numeric")

        ui.run()

        assertThat(printed.last()).contains("Invalid task id")
        coVerify(exactly = 1) { useCase.invoke("") }
    }

    @Test
    fun `numeric-only raw id throws InvalidTaskIdException`() = runTest {
        coEvery { reader.read() } returns "1234"
        coEvery { useCase.invoke("1234") } throws InvalidTaskIdException("numeric-only")
        ui.run()

        assertThat(printed.last()).contains("Invalid task id")
    }
}
