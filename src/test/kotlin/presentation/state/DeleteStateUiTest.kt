//package com.berlin.presentation.state
//
//import com.berlin.data.DummyData
//import com.berlin.domain.exception.InvalidStateIdException
//import com.berlin.domain.model.State
//import com.berlin.domain.usecase.state.DeleteStateUseCase
//import com.berlin.domain.usecase.state.GetAllStatesUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//
//
//class DeleteStateUiTest {
//
//    private val printed = mutableListOf<String>()
//    private val viewer: Viewer = mockk(relaxed = true) {
//        every { show(capture(printed)) } just Runs
//    }
//    private val reader: Reader = mockk()
//    private val deleteState: DeleteStateUseCase = mockk()
//    private val getAllStates: GetAllStatesUseCase = mockk()
//
//    private lateinit var state: State
//    private lateinit var ui: DeleteStateUi
//
//    @BeforeEach
//    fun setUp() {
//        DummyData.states.clear()
//        printed.clear()
//
//        state = State( "S1",  "To Do","L3")
//        DummyData.states += state
//
//        ui = DeleteStateUi(deleteState, getAllStates, viewer, reader)
//    }
//
//    @Test
//    fun `deleteState should deletes state and prints confirmation when confirm`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returnsMany listOf("1", "y")
//        every { deleteState.deleteState(state.id) } returns Result.success("Deleted Successfully")
//
//        //when
//        ui.run()
//
//        //Then
//        verify(exactly = 1) { deleteState.deleteState(state.id) }
//        assertThat(DummyData.states).doesNotContain(state)
//        assertThat(printed.last()).contains("Deleted.")
//    }
//
//    @Test
//    fun `run should return cancelled when user aborts deletion at confirmation`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returnsMany listOf("1", "n")
//
//        //when
//        ui.run()
//
//        //Then
//        verify(exactly = 0) { deleteState.deleteState(any()) }
//        assertThat(DummyData.states).contains(state)
//        assertThat(printed.last()).contains("Cancelled.")
//    }
//
//    @Test
//    fun `run should show Cancelled when user cancels in chooser`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returns "X"
//
//        //when
//        ui.run()
//
//        //Then
//        verify(exactly = 0) { deleteState.deleteState(any()) }
//        assertThat(printed.last()).contains("Cancelled.")
//    }
//
//    @Test
//    fun `delete state should return Deletion Failed when failed`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returnsMany listOf("1", "y")
//        every { deleteState.deleteState(state.id) } returns Result.failure(IllegalStateException("Deletion Failed"))
//
//        //when
//        ui.run()
//
//        //Then
//        verify(exactly = 1) { deleteState.deleteState(state.id) }
//        assertThat(DummyData.states).contains(state)
//        assertThat(printed.last()).contains("Deletion Failed")
//    }
//
//    @Test
//    fun `run should show error message Invalid selection when invalid index selected`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returns "99"
//
//        //when
//        ui.run()
//
//        //Then
//        verify(exactly = 0) { deleteState.deleteState(any()) }
//        assertThat(printed.last()).contains("Invalid selection")
//    }
//
//    @Test
//    fun `deleteState should throw InvalidStateIdExceptionwhen id is not valid`() {
//        //Given
//        every { getAllStates() } returns listOf(state)
//        every { reader.read() } returnsMany listOf("1", "y")
//        every { deleteState.deleteState(state.id) } throws InvalidStateIdException("State ID must not be empty or blank")
//
//        //when
//        ui.run()
//
//        //Then
//        assertThat(printed.last()).contains("invalid state id")
//        verify(exactly = 1) { deleteState.deleteState(state.id) }
//    }
//}
