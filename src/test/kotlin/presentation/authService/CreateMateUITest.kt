//package com.berlin.presentation.authService
//
//import com.berlin.domain.exception.InvalidAssigneeException
//import com.berlin.domain.helper.AuthServiceTestData
//import com.berlin.domain.usecase.authService.CreateMateUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import io.mockk.verifySequence
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class CreateMateUITest {
//
//    private lateinit var createMateUseCase: CreateMateUseCase
//    private lateinit var creationOfMateUi: CreateMateUI
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//
//
//
//    @BeforeEach
//    fun setup() {
//        createMateUseCase = mockk()
//        viewer = mockk(relaxed = true)
//        reader = mockk()
//        creationOfMateUi = CreateMateUI(createMateUseCase, viewer, reader)
//    }
//
//    @Test
//    fun `run should show success message when user creation succeeds`() {
//        every { reader.read() } returnsMany listOf(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword)
//        every { createMateUseCase.createMate(AuthServiceTestData.testForUserName, AuthServiceTestData.testForUserPassword) } returns Result.success(AuthServiceTestData.user)
//
//        creationOfMateUi.run()
//
//        verify { viewer.show("New mate is successfully created!") }
//    }
//
//    @Test
//    fun `run should retry once after failure and succeed second time`() {
//        every { reader.read() } returnsMany listOf("test1", "123", "test2", "456")
//        every { createMateUseCase.createMate("test1", "123") } returns Result.failure(InvalidAssigneeException("fail"))
//        every { createMateUseCase.createMate("test2", "456") } returns Result.success(AuthServiceTestData.expectedUser)
//
//        creationOfMateUi.run()
//
//        verifySequence {
//            viewer.show("Enter user name or x to exit: ")
//            viewer.show("Enter user password: ")
//            viewer.show("fail")
//
//            viewer.show("Enter user name or x to exit: ")
//            viewer.show("Enter user password: ")
//            viewer.show("New mate is successfully created!")
//        }
//    }
//
//
//    @Test
//    fun `run should treat null inputs as empty strings`() {
//        every { reader.read() } returnsMany listOf(null, null, "name", "pass")
//        every { createMateUseCase.createMate("", "") } returns Result.failure(InvalidAssigneeException("empty"))
//        every { createMateUseCase.createMate("name", "pass") } returns Result.success(AuthServiceTestData.expectedUser)
//
//        creationOfMateUi.run()
//
//        verify { viewer.show("empty") } // matching the actual error message
//        verify { viewer.show("New mate is successfully created!") }
//    }
//
//
//}
