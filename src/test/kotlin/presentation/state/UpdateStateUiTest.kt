package presentation.state

import com.berlin.data.DummyData
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.UpdateStateUi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateStateUiTest {
    private lateinit var updateState: UpdateStateUseCase
    private lateinit var getAllStates: GetAllStatesUseCase
    private lateinit var updateStateUi: UpdateStateUi
    private var viewer: Viewer = mockk(relaxed = true)
    private var reader: Reader = mockk()

    @BeforeEach
    fun setup() {
        updateState = mockk()
        getAllStates = mockk()
        updateStateUi = UpdateStateUi(updateState, getAllStates, viewer, reader)
    }

    /*
   1-successfully updated
   2-update failed
   3- InvalidStateNameException-->"State Name must not be empty or blank"
   4-InputCancelledException-->"Cancelled."
   5-InvalidSelectionException-->Invalid selection"
    */
    private companion object {
        val stateId = DummyData.states[1].id
        val successfullyStateNewName = "done"
        val stateProjectId = DummyData.states[1].projectId
        val emptyStateName = ""
    }

    @Test
    fun `run should return successfully updated when every thing is correct`() {
        //Given
        every { getAllStates() } returns DummyData.states
        every { reader.read() } returnsMany listOf("2", "done")
        every {
            updateState.updateState(any(), any(), any())
        } returns Result.success("Updated Successfully")

        //When
        updateStateUi.run()

        //Then
        verify { getAllStates() }
        verify { viewer.show(any()) }
        verify {
            updateState.updateState(
                stateId,
                successfullyStateNewName,
                stateProjectId
            )
        }
        verify { viewer.show("Updated Successfully") }
    }

    @Test
    fun `run should return update failed when update fails`() {
        //Given
        every { getAllStates() } returns DummyData.states
        every { reader.read() } returnsMany listOf("2", "done")
        every {
            updateState.updateState(any(), any(), any())
        } returns Result.failure(InvalidStateException("can not update state"))

        //When
        updateStateUi.run()

        //Then
        verify { viewer.show("Update Failed") }
    }

    @Test
    fun `run should throw InvalidStateNameException when State Name is empty or blank`() {
        //Given
        every { getAllStates() } returns DummyData.states
        every { reader.read() } returnsMany listOf("2", " ")
        every {
            updateState.updateState(any(), emptyStateName, any())
        } throws InvalidStateNameException("State Name must not be empty or blank")

        //When
        updateStateUi.run()

        //Then
        verify { viewer.show("State Name must not be empty or blank") }
    }

    @Test
    fun `run should `() {
        //Given
        every { reader.read() } returnsMany listOf("X")
        //When
        updateStateUi.run()

        //Then
        verify { viewer.show() }
    }
}