package presentation.authService

import com.berlin.domain.exception.UserNotFoundException
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.logic.helper.userDummyData
import com.berlin.model.Permission
import com.berlin.presentation.authService.GetUserByIDUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserByIDUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var UI: GetUserByIDUI
    private lateinit var useCase: GetUserByIDUseCase
    private val printed = mutableListOf<String>()

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true) {
            every { show(capture(printed)) } just Runs
        }
        reader = mockk()
        useCase = mockk()
        UI = GetUserByIDUI(useCase, viewer, reader)
        printed.clear()
    }
    @Test
    fun `GetUserByID should return user when user exists`(){
        //Given
        every { reader.read() } returns existingUserID
        every { useCase.getUserById(existingUserID) }

        //When
        val result=UI.run()

        //Then
        assertThat(result).isEqualTo(existingUserID)

    }
    @Test
    fun `GetUserByID should return when user doesn't exist`(){
        //Given
        every { reader.read() } returns "existingUserID"
        every { useCase.getUserById("existingUserID") } returns Result.failure(NoSuchElementException("No user found with ID: $nonExistingID"))

        //When
        val result=UI.run()

        //Then
        assertThat(printed).contains("No user found with ID: $nonExistingID")

    }

    @Test
    fun `GetUserByIDUI should return No User found when user id isn't exist`(){
        //Given
        every { reader.read() }returns nonExistingID
        every { useCase.getUserById(nonExistingID) } throws UserNotFoundException(nonExistingID)

        //when
        val result=UI.run()

        //Then
        assertThat(printed).contains("No user found with ID: $nonExistingID")
    }
    companion object{
        private val existingUserID="Men_11"
        private val nonExistingID="non_1"
        private val existingIDUser= userDummyData(
            "Men_123","Menna","1234", permission = Permission(), UserRole.ADMIN
        )
    }
}