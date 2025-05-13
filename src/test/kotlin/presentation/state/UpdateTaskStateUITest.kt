package com.berlin.presentation.state

import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateTaskStateUITest {
    private lateinit var updateStateUseCase: UpdateStateUseCase
    private lateinit var getAllStatesUseCase: GetAllStatesUseCase
    private lateinit var updateStateUI: UpdateStateUI
    private var viewer: Viewer = mockk(relaxed = true)
    private var reader: Reader = mockk()

    @BeforeEach
    fun setup() {
        updateStateUseCase = mockk()
        getAllStatesUseCase = mockk()
        updateStateUI = UpdateStateUI(updateStateUseCase, getAllStatesUseCase, viewer, reader)
    }



    @Test
    fun `run should throw InvalidStateNameException when State Name is empty or blank`() {
        //Given
        every { getAllStatesUseCase() } returns listOf(TaskState("Q1", "Menna", "P5"))
        every { reader.read() } returnsMany listOf("1", " ")
        every {
            updateStateUseCase(any(), emptyStateName, any())
        } throws InvalidStateNameException("State Name must not be empty or blank")

        //When
        updateStateUI.run()

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