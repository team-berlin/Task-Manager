//package com.berlin
//
//import com.berlin.data.DummyData
//import com.berlin.presentation.MainMenuUI
//import com.berlin.presentation.UiRunner
//import com.berlin.presentation.authService.AuthenticateUserUI
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import data.UserCache
//import io.mockk.*
//import org.junit.jupiter.api.Test
//import java.io.ByteArrayOutputStream
//import java.io.PrintStream
//
//class MainTest {
//
//    @Test
//    fun `main prints banner`() {
//        //when
//        val mockViewer = mockk<Viewer>(relaxed = true)
//        val mockReader = mockk<Reader>()
//        every { mockReader.read() } returns "X"
//        val mockAuthUi = mockk<AuthenticateUserUI> { every { run() } just Runs }
//        val mockUserCache = mockk<UserCache>()
//        every { mockUserCache.currentUser } returns DummyData.users.first()
//        val dummyRunners = emptyList<UiRunner>()
//        val mainMenuUI = MainMenuUI(
//            runners = dummyRunners,
//            viewer = mockViewer,
//            reader = mockReader,
//            authUi = mockAuthUi,
//            userCache = mockUserCache
//        )
//        val originalOut = System.out
//        val buffer = ByteArrayOutputStream()
//        System.setOut(PrintStream(buffer))
//
//        mainMenuUI.run()
//
//        System.setOut(originalOut)
//        verify { mockAuthUi.run() }
//    }
//}
