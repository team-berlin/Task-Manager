package com.berlin.presentation.authService

import com.berlin.domain.exception.InvalidAssigneeException
import com.berlin.domain.helper.AuthServiceTestData
import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreationOfMateUiTest {

    private lateinit var creationOfMateUseCase: CreationOfMateUseCase
    private lateinit var creationOfMateUi: CreationOfMateUi
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader



    @BeforeEach
    fun setup() {
        creationOfMateUseCase = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        creationOfMateUi = CreationOfMateUi(creationOfMateUseCase, viewer, reader)
    }

    @Test
    fun `run should show success message when user creation succeeds`() {
        every { reader.read() } returnsMany listOf(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword)
        every { creationOfMateUseCase.createMate(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword) } returns Result.success(AuthServiceTestData.user)

        creationOfMateUi.run()

        verify { viewer.show("New mate is successfully created!") }
    }

    @Test
    fun `run should retry once after failure and succeed second time`() {
        every { reader.read() } returnsMany listOf("test1", "123", "test2", "456")
        every { creationOfMateUseCase.createMate("test1", "123") } returns Result.failure(InvalidAssigneeException("fail"))
        every { creationOfMateUseCase.createMate("test2", "456") } returns Result.success(AuthServiceTestData.excepctedUser)

        creationOfMateUi.run()

        verifySequence {
            viewer.show("Enter user name: ")
            viewer.show("Enter user password: ")
            viewer.show("something wrong please try again!")

            viewer.show("Enter user name: ")
            viewer.show("Enter user password: ")
            viewer.show("New mate is successfully created!")
        }
    }


    @Test
    fun `run should treat null inputs as empty strings`() {
        every { reader.read() } returnsMany listOf(null, null, "name", "pass")
        every { creationOfMateUseCase.createMate("", "") } returns Result.failure(InvalidAssigneeException("empty"))
        every { creationOfMateUseCase.createMate("name", "pass") } returns Result.success(AuthServiceTestData.excepctedUser)

        creationOfMateUi.run()

        verify { viewer.show("something wrong please try again!") }
        verify { viewer.show("New mate is successfully created!") }
    }
}
