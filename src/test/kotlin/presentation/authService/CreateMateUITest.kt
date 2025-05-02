package presentation.authService
import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.fakeData.FakeHashingPassword
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.helper.AuthServiceTestData.excepctedUser
import com.berlin.domain.helper.AuthServiceTestData.testUserName
import com.berlin.domain.helper.AuthServiceTestData.testUserPassword
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.presentation.authService.CreationOfMateUi
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationOfMateUiTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var creationOfMateUi: CreationOfMateUi
    private lateinit var creationOfMateUseCase: CreationOfMateUseCase
    private lateinit var hashingPassword: HashingPassword
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader


    @BeforeEach
    fun setup() {
        authenticationRepository = mockk()
        hashingPassword = FakeHashingPassword()
        viewer = mockk(relaxed = true)
        reader = mockk()
        creationOfMateUseCase = CreationOfMateUseCase(authenticationRepository, hashingPassword)
        creationOfMateUi = CreationOfMateUi(creationOfMateUseCase, viewer, reader)
    }

    @Test
    fun `run should return create mate is success when user input valid data`() {
        every { viewer.show(any()) } just Runs

        val hashedPassword = hashingPassword.hashPassword(testUserPassword)

        every { reader.read() } returnsMany listOf(testUserName, testUserPassword)

        every { authenticationRepository.getAllUsers() } returns emptyList()

        every {
            authenticationRepository.createMate(
                testUserName, hashedPassword
            )
        } returns Result.success(excepctedUser)

        creationOfMateUi.run()

        verify { viewer.show("New mate is successfully created!") }
    }


    @Test
    fun `run should handle invalid data and prompt user to try again when enter invalid data`() {
        every { viewer.show(any()) } just Runs
        every { reader.read() } returnsMany listOf("", "")
        every { authenticationRepository.getAllUsers() } returns listOf(excepctedUser)
        every {
            authenticationRepository.createMate("", "")
        } returns Result.failure(InvalidAssigneeException("Password cannot be empty"))

        creationOfMateUi.run()

        verify(atLeast = 1) { viewer.show("something wrong please try again!") }
    }
    @Test
    fun `run should retry on failure then succeed on second attempt`() {
        every { viewer.show(any()) } just Runs

        val hashedPassword = hashingPassword.hashPassword(testUserPassword)

        every { reader.read() } returnsMany listOf(
            "", "",
            testUserName, testUserPassword)

        every { authenticationRepository.getAllUsers() } returns emptyList()

        every {
            authenticationRepository.createMate("", "")
        } returns Result.failure(InvalidAssigneeException("Password cannot be empty"))

        every {
            authenticationRepository.createMate(testUserName, hashedPassword)
        } returns Result.success(excepctedUser)

        creationOfMateUi.run()

        verify(exactly = 1) { viewer.show("something wrong please try again!") }
        verify { viewer.show("New mate is successfully created!") }
    }
    @Test
    fun `run should treat null inputs as empty strings`() {
        every { viewer.show(any()) } just Runs
        every { reader.read() } returnsMany listOf(null, null, testUserName, testUserPassword)
        every { authenticationRepository.getAllUsers() } returns emptyList()

        val hashedPassword = hashingPassword.hashPassword(testUserPassword)

        every { authenticationRepository.createMate("", "") } returns
                Result.failure(InvalidAssigneeException("empty input"))

        every { authenticationRepository.createMate(testUserName, hashedPassword) } returns
                Result.success(excepctedUser)

        creationOfMateUi.run()

        verify { viewer.show("something wrong please try again!") }
        verify { viewer.show("New mate is successfully created!") }
    }

}