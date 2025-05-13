package com.berlin.presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.state.DeleteTaskStateUseCase
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test



class DeleteTaskStateUiTest {

    private val printed = mutableListOf<String>()
    private val viewer: Viewer = mockk(relaxed = true) {
        every { show(capture(printed)) } just Runs
    }
    private val reader: Reader = mockk()
    private val deleteTaskStateUseCase: DeleteTaskStateUseCase = mockk()
    private val getAllTaskStatesUseCase: GetAllStatesUseCase = mockk()

    private lateinit var state: TaskState
    private lateinit var ui: DeleteStateUi

    @BeforeEach
    fun setUp() {
        DummyData.states.clear()
        printed.clear()

        state = TaskState("S1", "TODO", "P1")
        DummyData.states += state

        ui = DeleteStateUi(deleteTaskStateUseCase, getAllTaskStatesUseCase, viewer, reader)
    }

    @Test
    fun `deleteState should deletes state and prints confirmation when confirm`() {
        //Given
        val stateDeleted = TaskState("Q1","Menna","P5")

        every { getAllTaskStatesUseCase() } returns DummyData.states
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteTaskStateUseCase(state.id) } returns "Deleted Successfully"

        //when
        ui.run()

        //Then
        verify(exactly = 1) { deleteTaskStateUseCase(state.id) }
        assertThat(DummyData.states).doesNotContain(stateDeleted)
        assertThat(printed.last()).contains("Deleted.")
    }

    @Test
    fun `run should return cancelled when user aborts deletion at confirmation`() {
        //Given
        every { getAllTaskStatesUseCase() } returns listOf(state)
        every { reader.read() } returnsMany listOf("1", "n")

        //when
        ui.run()

        //Then
        verify(exactly = 0) { deleteTaskStateUseCase(any()) }
        assertThat(DummyData.states).contains(state)
        assertThat(printed.last()).contains("Cancelled.")
    }

    @Test
    fun `run should show Cancelled when user cancels in chooser`() {
        //Given
        every { getAllTaskStatesUseCase() } returns listOf(state)
        every { reader.read() } returns "X"

        //when
        ui.run()

        //Then
        verify(exactly = 0) { deleteTaskStateUseCase(any()) }
        assertThat(printed.last()).contains("Cancelled.")
    }


    @Test
    fun `run should show error message Invalid selection when invalid index selected`() {
        //Given
        every { getAllTaskStatesUseCase() } returns listOf(state)
        every { reader.read() } returns "99"

        //when
        ui.run()

        //Then
        verify(exactly = 0) { deleteTaskStateUseCase(any()) }
        assertThat(printed.last()).contains("Invalid selection")
    }

    @Test
    fun `deleteState should throw InvalidStateIdExceptionwhen id is not valid`() {
        //Given
        every { getAllTaskStatesUseCase() } returns listOf(state)
        every { reader.read() } returnsMany listOf("1", "y")
        every { deleteTaskStateUseCase(state.id) } throws InvalidStateIdException("State ID must not be empty or blank")

        //when
        ui.run()

        //Then
        assertThat(printed.last()).contains("invalid state id")
        verify(exactly = 1) { deleteTaskStateUseCase(state.id) }
    }
}
