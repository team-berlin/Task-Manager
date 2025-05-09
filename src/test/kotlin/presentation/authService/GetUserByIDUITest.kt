//package presentation.authService
//
//import com.berlin.domain.helper.AuthServiceTestData
//import com.berlin.domain.usecase.authService.GetUserByIDUseCase
//import com.berlin.presentation.authService.GetUserByIDUI
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import kotlin.test.Test
//
//
//class GetUserByIDUITest {
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var useCase: GetUserByIDUseCase
//    private lateinit var ui: GetUserByIDUI
//    private val printed = mutableListOf<String>()
//
//    @BeforeEach
//    fun setUp() {
//        viewer = mockk(relaxed = true) {
//            every { show(capture(printed)) } just Runs
//        }
//        reader = mockk()
//        useCase = mockk()
//        ui = GetUserByIDUI(useCase, viewer, reader)
//        printed.clear()
//    }
//
//
//    @Test
//    fun `should call use case when correct user ID`() {
//        // Given
//        val id = AuthServiceTestData.idExist
//        every { reader.read() } returns id
//        every { useCase.getUserById(id) } returns Result.success(AuthServiceTestData.existingUser)
//
//        // When
//        ui.run()
//
//        // Then
//        verify { useCase.getUserById(id) }
//        assertThat(printed).contains("Enter the user id: ")
//    }
//
//
//}