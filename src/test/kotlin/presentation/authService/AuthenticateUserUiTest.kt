package presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.helper.AuthServiceTestData.testUserName
import com.berlin.domain.helper.AuthServiceTestData.testUserPassword
import com.berlin.domain.helper.AuthServiceTestData.user
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.presentation.authService.AuthenticateUserUi
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticateUserUiTest{
    private lateinit var authenticateUserUi: AuthenticateUserUi
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase
    private lateinit var hashingPassword: HashingPassword
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader

    @BeforeEach
    fun setup(){
        authenticationRepository = mockk()
        authenticateUserUseCase = mockk()
        hashingPassword = mockk()
        viewer = mockk()
        reader = mockk()
        authenticateUserUi = AuthenticateUserUi(authenticateUserUseCase,viewer,reader)

    }

    @Test
    fun `validateUser should return user  if user enter valid data`(){
        every { viewer.show(any()) } just Runs
        every { reader.read() } returnsMany  listOf(testUserName, testUserPassword)

        every { authenticateUserUseCase.login(testUserName , testUserPassword) } returns Result.success(user)
        authenticateUserUi.run()
        verify(exactly = 1) { viewer.show("Welcome Fatma") }
        verify { authenticateUserUseCase.login(testUserName , testUserPassword) }


    }
    @Test
    fun `validateUser should return failed if user didn't enter valid data`(){
        val inValidUserName = "Ahmed"
        val inValidUserPassword = "41325647"
        every { viewer.show(any()) } just Runs
        every { reader.read() } returnsMany listOf(inValidUserName, inValidUserPassword)
        every { authenticateUserUseCase.login("", any())
        } returns Result.failure(InvalidCredentialsException("not user found"))
        authenticateUserUi.run()
        verify { viewer.show("something wrong please try again") }
        verify { authenticateUserUseCase.login(testUserName, testUserPassword) }

    }

}