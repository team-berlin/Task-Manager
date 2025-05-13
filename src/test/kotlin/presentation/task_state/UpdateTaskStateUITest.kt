package com.berlin.presentation.task_state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.task_state.GetAllTaskStatesUseCase
import com.berlin.domain.usecase.task_state.UpdateTaskStateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateTaskStateUITest {
    private lateinit var updateTaskStateUseCase: UpdateTaskStateUseCase
    private lateinit var getAllTaskStatesUseCase: GetAllTaskStatesUseCase
    private lateinit var updateTaskStateUI: UpdateTaskStateUI
    private var viewer: Viewer = mockk(relaxed = true)
    private var reader: Reader = mockk()

    @BeforeEach
    fun setup() {
        updateTaskStateUseCase = mockk()
        getAllTaskStatesUseCase = mockk()
        updateTaskStateUI = UpdateTaskStateUI(updateTaskStateUseCase, getAllTaskStatesUseCase, viewer, reader)
    }



    @Test
    fun `run should throw InvalidStateNameException when State Name is empty or blank`() {
        //Given
        every { getAllTaskStatesUseCase() } returns listOf(TaskState("Q1", "Menna", "P5"))
        every { reader.read() } returnsMany listOf("1", " ")
        every {
            updateTaskStateUseCase(any(), emptyStateName, any())
        } throws InvalidStateNameException("State Name must not be empty or blank")

        //When
        updateTaskStateUI.run()

        //Then
        verify { viewer.show("State Name must not be empty or blank") }
    }

    private companion object {
        private val state = TaskState("Q1", "Menna", "P5")
        private val states = listOf(state)
        private const val successfullyStateNewName = "done"
        private const val emptyStateName = ""
    }

}