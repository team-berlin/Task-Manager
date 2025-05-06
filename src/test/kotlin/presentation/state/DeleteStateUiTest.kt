package presentation.state

import com.berlin.domain.usecase.state.DeleteStateUseCase
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.state.DeleteStateUi

import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach

class DeleteStateUiTest{
  private lateinit var deleteStateUseCase: DeleteStateUseCase
  private lateinit var getAllStates: GetAllStatesUseCase
  private lateinit var deleteStateUi: DeleteStateUi
  private val viewer: Viewer = mockk(relaxed = true)
  private val reader: Reader = mockk(relaxed = true)

  @BeforeEach
  fun setup() {
   deleteStateUseCase = mockk(relaxed = true)
   getAllStates = mockk(relaxed = true)
   deleteStateUi = DeleteStateUi(deleteStateUseCase, getAllStates, viewer, reader)
  }

 }