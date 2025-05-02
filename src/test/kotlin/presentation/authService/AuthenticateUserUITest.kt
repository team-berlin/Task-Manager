package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidCredentialsException
import com.berlin.domain.helper.AuthServiceTestData.testUserName
import com.berlin.domain.helper.AuthServiceTestData.testUserPassword
import com.berlin.domain.helper.AuthServiceTestData.user
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import domain.usecase.authService.AuthenticateUserUseCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AuthenticateUserUiTest {
    private lateinit var authenticateUserUi: AuthenticateUserUi
    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader

    private val printedMessages = mutableListOf<String>()

    @BeforeEach
    fun setup() {
        authenticateUserUseCase = mockk()
        viewer = mockk {
            every { show(capture(printedMessages)) } just Runs
        }
        reader = mockk()
        authenticateUserUi = AuthenticateUserUi(authenticateUserUseCase, viewer, reader)
    }

    @Test
    fun `should authenticate successfully with valid credentials`() {
        every { reader.read() } returnsMany listOf(testUserName, testUserPassword)

        every {
            authenticateUserUseCase.login(testUserName, testUserPassword)
        } returns Result.success(user)

        authenticateUserUi.run()

        verify { authenticateUserUseCase.login(testUserName, testUserPassword) }
        assert(printedMessages.contains("Welcome ${user.userName}"))
    }

    @Test
    fun `should retry on invalid credentials then succeed`() {
        every { reader.read() } returnsMany listOf("wrongUser", "wrongPass", testUserName, testUserPassword)

        every {
            authenticateUserUseCase.login("wrongUser", "wrongPass")
        } returns Result.failure(InvalidCredentialsException("Invalid"))

        every {
            authenticateUserUseCase.login(testUserName, testUserPassword)
        } returns Result.success(user)

        authenticateUserUi.run()

        verify { viewer.show("Try again") }
        verify { viewer.show("Welcome ${user.userName}") }
    }

    @Test
    fun `should treat null inputs as empty and retry then succeed`() {
        every { reader.read() } returnsMany listOf(null, null, testUserName, testUserPassword)

        every {
            authenticateUserUseCase.login("", "")
        } returns Result.failure(InvalidCredentialsException("Empty"))

        every {
            authenticateUserUseCase.login(testUserName, testUserPassword)
        } returns Result.success(user)

        authenticateUserUi.run()

        verify { viewer.show("Try again") }
        verify { viewer.show("Welcome ${user.userName}") }
    }
}
