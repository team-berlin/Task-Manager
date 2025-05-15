package com.berlin.presentation.task_state

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
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
    fun `run should throw InvalidStateNameException when State Name is null, empty, or blank`() {
        //Given
        every { getAllTaskStatesUseCase() } returns states
        every { reader.read() } returnsMany listOf("1", null)
        every {
            updateTaskStateUseCase(any(), emptyStateName, any())
        } throws InvalidStateNameException("State Name must not be empty or blank")

        //When
        updateTaskStateUI.run()

        //Then
        verify { viewer.show("State Name must not be empty or blank") }
    }

    @Test
    fun `updateState should cancel the update when user enters x`() {
        //Given
        every { getAllTaskStatesUseCase() } returns states
        every { reader.read() } returnsMany listOf("1", "x")
        every {
            updateTaskStateUseCase(any(), "x", any())
        } throws InputCancelledException("Cancelled!")

        //When
        updateTaskStateUI.run()

        //Then
        verify { viewer.show("Cancelled!") }
    }


    @Test
    fun `updateState should trow invalid selection exception when the user choose invalid selection`() {
        //Given
        every { getAllTaskStatesUseCase() } returns states
        every { reader.read() } returnsMany listOf("00", newValidStateName)
        every {
            updateTaskStateUseCase(any(), newValidStateName, any())
        } throws InvalidSelectionException("invalid selection")

        //When
        updateTaskStateUI.run()

        //Then
        verify { viewer.show("invalid selection") }
    }

    @Test
    fun `updateState should return success when the new state name is valid`() {
        //Given
        every { getAllTaskStatesUseCase() } returns states
        every { reader.read() } returnsMany listOf("1", newValidStateName)
        every {
            updateTaskStateUseCase(any(), newValidStateName, any())
        } returns "$newValidStateName is updated Successfully"

        //When
        updateTaskStateUI.run()

        //Then
        verify { viewer.show("$newValidStateName is updated Successfully") }
    }


    private companion object {
        private val state = TaskState("Q1", "Menna", "P5")
        private val states = listOf(state)
        private const val newValidStateName = "new state"
        private const val emptyStateName = ""
    }

}