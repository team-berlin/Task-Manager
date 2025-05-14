package com.berlin.presentation.task_state


import com.berlin.domain.model.TaskState
import com.berlin.domain.usecase.task_state.GetTaskStateByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTaskStateByIdUiTest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getTaskStateByIdUseCase: GetTaskStateByIdUseCase
    private lateinit var getTaskStateByIdUi: GetTaskStateByIdUi
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setup() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        getTaskStateByIdUseCase = mockk()
        getTaskStateByIdUi = GetTaskStateByIdUi(getTaskStateByIdUseCase, viewer, reader)
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
        every { getTaskStateByIdUseCase("Q1") } returns stateExists
        every { reader.read() } returns "Q1"
        //when
        getTaskStateByIdUi.run()
        //Then
        verify { viewer.show("Enter state ID: ") }

    }


}