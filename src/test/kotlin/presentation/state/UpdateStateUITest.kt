package presentation.state

import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.InvalidStateNameException
import com.berlin.domain.model.State
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.UpdateStateUI
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateStateUITest {
    private lateinit var updateState: UpdateStateUseCase
    private lateinit var getAllStates: GetAllStatesUseCase
    private lateinit var updateStateUi: UpdateStateUI
    private var viewer: Viewer = mockk(relaxed = true)
    private var reader: Reader = mockk()

    @BeforeEach
    fun setup() {
        updateState = mockk()
        getAllStates = mockk()
        updateStateUi = UpdateStateUI(updateState, getAllStates, viewer, reader)
    }


    @Test
    fun `run should return successfully updated when every thing is correct`() {
        //Given
        every { getAllStates() } returns states
        every { reader.read() } returnsMany listOf("1", "done")
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
                state.id,
                successfullyStateNewName,
                state.projectId
            )
        }
        verify { viewer.show("Updated Successfully") }
    }

    @Test
    fun `run should return update failed when update fails`() {
        //Given
        every { getAllStates() } returns states
        every { reader.read() } returnsMany listOf("1", "done")
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
        every { getAllStates() } returns listOf(State("Q1", "Menna", "P5"))
        every { reader.read() } returnsMany listOf("1", " ")
        every {
            updateState.updateState(any(), emptyStateName, any())
        } throws InvalidStateNameException("State Name must not be empty or blank")

        //When
        updateStateUi.run()

        //Then
        verify { viewer.show("State Name must not be empty or blank") }
    }

    private companion object {
        private val state = State("Q1", "Menna", "P5")
        private val states = listOf(state)
        private const val successfullyStateNewName = "done"
        private const val emptyStateName = ""
    }

}