package presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.usecase.state.GetStateByIdUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.GetStateByIdUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetStateByIdUiTest {
    /*
   1-id not exist so state not exist
   2-id exist
    */
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getStateById: GetStateByIdUseCase
    private lateinit var ui: GetStateByIdUi

    @BeforeEach
    fun setup() {
        viewer = mockk(relaxed = true)
        reader = mockk()
        getStateById = mockk()
        ui = GetStateByIdUi(getStateById, viewer, reader)
    }

    @Test
    fun `getStateById should return state when its id exists`() {
        //given
        every { getStateById.getStateById("Q1") } returns Result.success(DummyData.states[1])
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
        every { reader.read() } returns "Q999"
        every { getStateById.getStateById("Q999") } returns Result.failure(StateNotFoundException("State with ID Q999 not found"))

        // When
        ui.run()

        // Then
        verify { viewer.show("Enter state ID: ") }
        verify { getStateById.getStateById("Q999") }
        verify { viewer.show("No task found with ID “Q999”") }
    }

}