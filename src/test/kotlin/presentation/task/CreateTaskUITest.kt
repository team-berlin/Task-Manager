//package com.berlin.presentation.task
//
//import com.berlin.data.DummyData
//import com.berlin.domain.exception.InvalidTaskTitle
//import com.berlin.domain.exception.TaskAlreadyExistsException
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.usecase.task.CreateTaskUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class CreateTaskUITest {
//
//    private val printed = mutableListOf<String>()
//    private val viewer: Viewer = mockk(relaxed = true) {
//        every { show(capture(printed)) } just Runs
//    }
//    private val reader: Reader = mockk()
//    private val createUC: CreateTaskUseCase = mockk()
//    private val currentUser: User = DummyData.users.first()
//    private lateinit var ui: CreateTaskUI
//
//    @BeforeEach
//    fun reset() {
//        DummyData.tasks.clear()
//        printed.clear()
//        ui = CreateTaskUI(createUC, currentUser, viewer, reader)
//    }
//
//    @Test
//    fun `creates task and prints success`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "Feature X", "")
//        every { createUC.invoke(any(), any(), any(), any(), any(), any()) } answers {
//            Result.success(
//                Task(
//                    id = "T42",
//                    projectId = firstArg(),
//                    title = secondArg(),
//                    description = arg(2),
//                    stateId = arg(3),
//                    assignedToUserId = arg(5),
//                    createByUserId = arg(4)
//                )
//            )
//        }
//
//        ui.run()
//
//        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last()).contains("Task created: id=T42")
//    }
//
//    @Test
//    fun `prints Cancelled when user aborts`() {
//        every { reader.read() } returns "X"
//
//        ui.run()
//
//        verify(exactly = 0) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last()).contains("Cancelled.")
//    }
//
//    @Test
//    fun `empty title shows invalid selection message`() {
//        // project, state, user selected; then blank title
//        every { reader.read() } returnsMany listOf("1", "1", "1", "")
//
//        ui.run()
//
//        verify(exactly = 0) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last()).contains("Invalid selection")
//    }
//
//    @Test
//    fun `failure from use case is printed`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "Bug fix", "")
//        every {
//            createUC.invoke(
//                any(), any(), any(), any(), any(), any()
//            )
//        } returns Result.failure(IllegalStateException("db down"))
//
//        ui.run()
//
//        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last()).contains("db down")
//    }
//
//    @Test
//    fun `invalid user index prints error message`() {
//        // project=1, state=1, then bad user index=99
//        every { reader.read() } returnsMany listOf("1", "1", "99")
//
//        ui.run()
//
//        verify(exactly = 0) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last().lowercase()).contains("invalid selection")
//    }
//
//    @Test
//    fun `invalid state index prints error message`() {
//        every { reader.read() } returnsMany listOf("1", "57")
//
//        ui.run()
//
//        verify(exactly = 0) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last().lowercase()).contains("invalid selection")
//    }
//
//    @Test
//    fun `invalid project index prints error message`() {
//        every { reader.read() } returns "79"
//
//        ui.run()
//
//        verify(exactly = 0) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//        assertThat(printed.last().lowercase()).contains("invalid selection")
//    }
//
//    @Test
//    fun `null description still creates task`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "Doc title", null)
//        every { createUC.invoke(any(), any(), null, any(), any(), any()) } returns Result.success(
//            Task(
//                id = "T99",
//                projectId = "P1",
//                title = "Doc title",
//                description = null,
//                stateId = "S1",
//                assignedToUserId = DummyData.users[1].id,
//                createByUserId = currentUser.id
//            )
//        )
//
//        ui.run()
//
//        verify { createUC.invoke(any(), any(), null, any(), any(), any()) }
//        assertThat(printed.last()).contains("Task created: id=T99")
//    }
//
//    @Test
//    fun `shows InvalidTaskTitle when use case throws that exception`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "BadTitle", "")
//
//        every {
//            createUC.invoke(any(), any(), any(), any(), any(), any())
//        } throws InvalidTaskTitle("title ruled invalid")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Invalid task title")
//        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//    }
//
//    @Test
//    fun `shows TaskAlreadyExistsException when use case returns failure`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "MyTask", "")
//        every { createUC.invoke(any(), any(), any(), any(), any(), any()) } returns Result.failure(
//            TaskAlreadyExistsException("the task already existed")
//        )
//
//        ui.run()
//
//        assertThat(printed.last()).contains("the task already existed")
//        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//    }
//
//    @Test
//    fun `use case throwing TaskAlreadyExistsException is caught and shows existed message`() {
//        every { reader.read() } returnsMany listOf("1", "1", "1", "MyTitle", "")
//
//        every {
//            createUC.invoke(any(), any(), any(), any(), any(), any())
//        } throws TaskAlreadyExistsException("already there")
//
//        ui.run()
//
//        verify(exactly = 1) { createUC.invoke(any(), any(), any(), any(), any(), any()) }
//
//        assertThat(printed.last()).contains("the task already existed")
//    }
//
//}
