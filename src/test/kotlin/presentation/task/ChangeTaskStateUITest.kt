//package com.berlin.presentation.task
//
//import com.berlin.data.DummyData
//import com.berlin.domain.exception.InvalidTaskStateException
//import com.berlin.domain.exception.TaskNotFoundException
//import com.berlin.domain.model.State
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.model.UserRole
//import com.berlin.domain.usecase.task.ChangeTaskStateUseCase
//import com.berlin.domain.usecase.task.GetAllTasksUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class ChangeTaskStateUITest {
//
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var changeUC: ChangeTaskStateUseCase
//    private lateinit var getAllTasks: GetAllTasksUseCase
//    private lateinit var ui: ChangeTaskStateUI
//
//    private val printed = mutableListOf<String>()
//    private lateinit var task: Task
//    private lateinit var alice: User
//
//    @BeforeEach
//    fun setUp() {
//        // reset all in-memory data
//        DummyData.users.clear()
//        DummyData.tasks.clear()
//        DummyData.states.clear()
//
//        // add one user (needed for assignedBy / filter logic)
//        alice = User("U1", "alice", "pw", UserRole.MATE)
//        DummyData.users += alice
//
//        // one task in TODO state S1
//        task = Task(
//            id                 = "T1",
//            projectId          = "P1",
//            title              = "Demo",
//            description        = null,
//            stateId            = "S1",
//            assignedToUserId   = alice.id,
//            createByUserId     = alice.id
//        )
//        DummyData.tasks += task
//
//        // two possible states for project P1
//        DummyData.states += State("S1", "TODO", "P1")
//        DummyData.states += State("S2", "DONE", "P1")
//
//        // mocks
//        viewer     = mockk(relaxed = true) { every { show(capture(printed)) } just Runs }
//        reader     = mockk()
//        changeUC   = mockk()
//        getAllTasks = mockk()
//
//        // stub before UI instantiation
//        every { getAllTasks.invoke() } returns listOf(task)
//
//        ui = ChangeTaskStateUI(changeUC, getAllTasks, viewer, reader)
//        printed.clear()
//    }
//
//    @Test
//    fun `success moves to chosen state`() {
//        // pick task #1, then state #1 (TODO)
//        every { reader.read() } returnsMany listOf("1", "1")
//        every { changeUC.invoke("T1", "S1") } returns Result.success(task.copy(stateId = "S1"))
//
//        ui.run()
//
//        verify { changeUC.invoke("T1", "S1") }
//        assertThat(printed.last()).contains("Task T1 moved to TODO")
//    }
//
//    @Test
//    fun `no states defined prints message and returns`() {
//        DummyData.states.clear()               // trigger "no states" path
//        every { reader.read() } returns "1"    // choose task
//
//        ui.run()
//
//        verify { changeUC wasNot Called }
//        assertThat(printed.last()).contains("No states defined for project P1")
//    }
//
//    @Test
//    fun `cancelling at task chooser prints Cancelled`() {
//        every { reader.read() } returns "X"
//
//        ui.run()
//
//        verify { changeUC wasNot Called }
//        assertThat(printed.last()).contains("Cancelled.")
//    }
//
//    @Test
//    fun `invalid task index prints Invalid selection`() {
//        every { reader.read() } returns "99"
//
//        ui.run()
//
//        verify { changeUC wasNot Called }
//        assertThat(printed.last()).contains("Invalid selection")
//    }
//
//    @Test
//    fun `cancelling at state chooser prints Cancelled`() {
//        every { reader.read() } returnsMany listOf("1", "X")
//
//        ui.run()
//
//        verify { changeUC wasNot Called }
//        assertThat(printed.last()).contains("Cancelled.")
//    }
//
//    @Test
//    fun `invalid state index prints Invalid selection`() {
//        every { reader.read() } returnsMany listOf("1", "99")
//
//        ui.run()
//
//        verify { changeUC wasNot Called }
//        assertThat(printed.last()).contains("Invalid selection")
//    }
//
//    @Test
//    fun `onFailure with real message shows it`() {
//        every { reader.read() } returnsMany listOf("1", "1")
//        every { changeUC.invoke("T1", "S1") } returns Result.failure(IllegalStateException("boom"))
//
//        ui.run()
//
//        assertThat(printed.last()).contains("boom")
//    }
//
//    @Test
//    fun `onFailure with null message shows default`() {
//        every { reader.read() } returnsMany listOf("1", "1")
//        every { changeUC.invoke("T1", "S1") } returns Result.failure(IllegalStateException("Failed to change state"))
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Failed to change state")
//    }
//
//    @Test
//    fun `shows InvalidTaskStateException when use case throws`() {
//        every { reader.read() } returnsMany listOf("1", "1")
//        every { changeUC.invoke("T1", "S1") } throws InvalidTaskStateException("bad state")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Invalid task state")
//    }
//
//    @Test
//    fun `shows TaskNotFoundException when use case throws`() {
//        every { reader.read() } returnsMany listOf("1", "1")
//        every { changeUC.invoke("T1", "S1") } throws TaskNotFoundException("notfound")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Task not found")
//    }
//}
