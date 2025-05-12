//package com.berlin.presentation.task
//
//import com.berlin.domain.exception.InvalidAssigneeException
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.model.UserRole
//import com.berlin.domain.usecase.authService.GetAllUsersUseCase
//import com.berlin.domain.usecase.task.AssignTaskUseCase
//import com.berlin.domain.usecase.task.GetAllTasksUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class AssignTaskUITest {
//
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var assignTaskUC: AssignTaskUseCase
//    private lateinit var getAllTasks: GetAllTasksUseCase
//    private lateinit var fetchUsers: GetAllUsersUseCase
//    private lateinit var ui: AssignTaskUI
//
//    // capture all calls to viewer.show(...)
//    private val printed = mutableListOf<String>()
//
//    // shared test data
//    private val task = Task(
//        id = "T1",
//        projectId = "P1",
//        title = "Demo",
//        description = null,
//        stateId = "TODO",
//        assignedToUserId = "U1",
//        createByUserId = "U1"
//    )
//    private val user1 = User(id = "U1", userName = "alice", Apassword = "pw", role = UserRole.MATE)
//    private val user2 = User(id = "U2", userName = "bob", password = "pw", role = UserRole.MATE)
//
//    @BeforeEach
//    fun setUp() {
//        viewer = mockk(relaxed = true) {
//            every { show(capture(printed)) } just Runs
//        }
//        reader = mockk()
//        assignTaskUC = mockk()
//        getAllTasks = mockk()
//        fetchUsers = mockk()
//
//        // stub out task‐list and user‐list
//        every { getAllTasks.invoke() } returns listOf(task)
//        every { fetchUsers.getAllUsers() } returns Result.success(listOf(user1, user2))
//
//        ui = AssignTaskUI(assignTaskUC, getAllTasks, fetchUsers, viewer, reader)
//        printed.clear()
//    }
//
//    /** drive the two reader.read() calls: task‐choice, then assignee‐choice */
//    private fun stubReads(vararg inputs: String) {
//        every { reader.read() } returnsMany inputs.toList()
//    }
//
//    @Test
//    fun `success prints Assigned to userName`() {
//        stubReads("1", "2")
//        every { assignTaskUC.invoke(task.id, user2.id) }.returns(Result.success(task.copy(assignedToUserId = user2.id)))
//
//        ui.run()
//
//        verify(exactly = 1) { assignTaskUC.invoke("T1", "U2") }
//        assertThat(printed.last()).isEqualTo("Assigned to bob")
//    }
//
//    @Test
//    fun `failure with message prints that message`() {
//        stubReads("1", "2")
//        every { assignTaskUC.invoke(any(), any()) }.returns(Result.failure(IllegalStateException("cant assign")))
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("cant assign")
//    }
//
//    @Test
//    fun `failure without message prints default`() {
//        stubReads("1", "2")
//        every { assignTaskUC.invoke(any(), any()) }.returns(Result.failure(RuntimeException()))
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("Assignment failed")
//    }
//
//    @Test
//    fun `throws InvalidAssigneeException prints Invalid assignee`() {
//        stubReads("1", "2")
//        every { assignTaskUC.invoke(task.id, user2.id) }.throws(InvalidAssigneeException("nope"))
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("Invalid assignee")
//        verify(exactly = 1) { assignTaskUC.invoke("T1", "U2") }
//    }
//
//    @Test
//    fun `cancel in task chooser prints Cancelled`() {
//        every { reader.read() } returns "X"
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("Cancelled.")
//        verify { assignTaskUC wasNot Called }
//    }
//
//    @Test
//    fun `cancel in assignee chooser prints Cancelled`() {
//        stubReads("1", "X")
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("Cancelled.")
//        verify { assignTaskUC wasNot Called }
//    }
//
//    @Test
//    fun `invalid task index prints Invalid selection`() {
//        every { reader.read() } returns "99"
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("Invalid selection")
//        verify { assignTaskUC wasNot Called }
//    }
//}
