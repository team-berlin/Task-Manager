package com.berlin.presentation.state


import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.state.GetStateByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTaskStateByIdUiTest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getStateById: GetStateByIdUseCase
    private lateinit var ui: GetStateByIdUi
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setup() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        getStateById = mockk()
        ui = GetStateByIdUi(getStateById, viewer, reader)
        printed.clear()
    }

    private companion object {
        val stateExists = TaskState("Q1", "Menna", "P5")
        val stateIdNotExist = "Q999"
        val idWhenExceptionNull = "T3"
    }

    @Test
    fun `getStateById should return state when its id exists`() {
        //given
        every { getStateById.getStateById("Q1") } returns Result.success(stateExists)
        every { reader.read() } returns "Q1"
        //when
        ui.run()
        //Then
        verify { viewer.show("Enter state ID: ") }
        verify { getStateById.getStateById("Q1") }

    }

    @Test
    fun `getStateById should show message when id doesn't exist`() {
        // Given
        every { reader.read() } returns stateIdNotExist
        every { getStateById.getStateById(stateIdNotExist) } returns Result.failure(StateNotFoundException("State with ID Q999 not found"))

        // When
        ui.run()

        // Then
        assertThat(printed.last()).isEqualTo("No state found with ID “Q999”")
    }

    @Test
    fun `lookup failed fallback when exception message null`() {
        //given
        every { reader.read() } returns idWhenExceptionNull
        every { getStateById.getStateById(idWhenExceptionNull) } returns Result.failure(IllegalStateException("Lookup failed"))

        //When
        ui.run()

        //Then
        assertThat(printed.last()).contains("Lookup failed")
    }

}