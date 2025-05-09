//package presentation.task
//
//
//import com.berlin.data.DummyData
//import com.berlin.domain.exception.InvalidTaskTitle
//import com.berlin.domain.exception.TaskNotFoundException
//import com.berlin.domain.model.Task
//import com.berlin.domain.model.User
//import com.berlin.domain.model.UserRole
//import com.berlin.domain.usecase.task.GetAllTasksUseCase
//import com.berlin.domain.usecase.task.UpdateTaskUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.berlin.presentation.task.UpdateTaskUI
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class UpdateTaskUITest {
//
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var updateUC: UpdateTaskUseCase
//    private lateinit var getAllTasks: GetAllTasksUseCase
//
//    private lateinit var ui: UpdateTaskUI
//
//    private val printed = mutableListOf<String>()
//    private lateinit var existing: Task
//    private lateinit var newUser: User
//
//    @BeforeEach
//    fun setUp() {
//        // reset in-memory data
//        DummyData.tasks.clear()
//        DummyData.users.clear()
//        // add two users
//        DummyData.users += User("U1", "alice", "pw", UserRole.MATE)
//        DummyData.users += User("U2", "bob", "pw",  UserRole.MATE)
//
//        // add one existing task
//        existing = Task(
//            id = "T1",
//            projectId = "P1",
//            title = "OldTitle",
//            description = "OldDesc",
//            stateId = "TODO",
//            assignedToUserId = "U1",
//            createByUserId = "U1"
//        )
//        DummyData.tasks += existing
//        newUser = DummyData.users[1]  // bob
//
//        // mock IO and use-case
//        viewer = mockk(relaxed = true) {
//            every { show(capture(printed)) } just Runs
//        }
//        reader = mockk()
//        updateUC = mockk()
//        getAllTasks = mockk()
//
//        every { getAllTasks.invoke() } returns listOf(existing)
//
//        ui = UpdateTaskUI(updateUC, getAllTasks, viewer, reader)
//
//        printed.clear()
//    }
//
//    @Test
//    fun `success when title only changes`() {
//        every { reader.read() } returnsMany listOf(
//            "1",
//            "NewTitle",
//            "",
//            "X"
//        )
//        every {
//            updateUC.invoke("T1", title = "NewTitle", description = null, assignedToUserId = null)
//        } returns Result.success(existing.copy(title = "NewTitle"))
//
//        ui.run()
//
//        verify {
//            updateUC.invoke("T1", title = "NewTitle", description = null, assignedToUserId = null)
//        }
//        assertThat(printed).contains("Task updated: T1")
//    }
//
//    @Test
//    fun `success when description only changes`() {
//        every { reader.read() } returnsMany listOf(
//            "1",
//            "",
//            "NewDesc",
//            "X"
//        )
//        every {
//            updateUC.invoke("T1", title = null, description = "NewDesc", assignedToUserId = null)
//        } returns Result.success(existing.copy(description = "NewDesc"))
//
//        ui.run()
//
//        verify {
//            updateUC.invoke("T1", title = null, description = "NewDesc", assignedToUserId = null)
//        }
//        assertThat(printed).contains("Task updated: T1")
//    }
//
//    @Test
//    fun `success when assignee only changes`() {
//        every { reader.read() } returnsMany listOf(
//            "1",
//            "",
//            "",
//            "2"
//        )
//        every {
//            updateUC.invoke("T1", title = null, description = null, assignedToUserId = "U2")
//        } returns Result.success(existing.copy(assignedToUserId = "U2"))
//
//        ui.run()
//
//        verify {
//            updateUC.invoke("T1", title = null, description = null, assignedToUserId = "U2")
//        }
//        assertThat(printed).contains("Task updated: T1")
//    }
//
//    @Test
//    fun `success when nothing changes`() {
//        every { reader.read() } returnsMany listOf(
//            "1", "", "", "X"
//        )
//        every {
//            updateUC.invoke("T1", title = null, description = null, assignedToUserId = null)
//        } returns Result.success(existing)
//
//        ui.run()
//
//        verify {
//            updateUC.invoke("T1", title = null, description = null, assignedToUserId = null)
//        }
//        assertThat(printed).contains("Task updated: T1")
//    }
//
//    @Test
//    fun `failure from use case is printed`() {
//        every { reader.read() } returnsMany listOf("1", "", "", "X")
//        every {
//            updateUC.invoke("T1", any(), any(), any())
//        } returns Result.failure(IllegalStateException("boom"))
//
//        ui.run()
//
//        assertThat(printed.last()).contains("boom")
//        verify { updateUC.invoke("T1", null, null, null) }
//    }
//
//    @Test
//    fun `fallback message when failure message null`() {
//        every { reader.read() } returnsMany listOf("1", "", "", "X")
//        every {
//            updateUC.invoke("T1", any(), any(), any())
//        } returns Result.failure(IllegalStateException("Update failed"))
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Update failed")
//    }
//
//    @Test
//    fun `cancelling at task chooser prints Cancelled`() {
//        every { reader.read() } returns "X"
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Cancelled.")
//        verify { updateUC wasNot Called }
//    }
//
//    @Test
//    fun `invalid task index prints Invalid selection`() {
//        every { reader.read() } returns "foo"
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Invalid selection")
//        verify { updateUC wasNot Called }
//    }
//
//    @Test
//    fun `invalid assignee index prints Invalid selection`() {
//        every { reader.read() } returnsMany listOf("1", "", "", "99")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Invalid selection")
//        verify { updateUC wasNot Called }
//    }
//
//    @Test
//    fun `shows InvalidTaskTitle when use case throws`() {
//        every { reader.read() } returnsMany listOf("1", "Bad!", "", "X")
//        every {
//            updateUC.invoke(any(), any(), any(), any())
//        } throws InvalidTaskTitle("no digits")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Invalid task title")
//    }
//
//    @Test
//    fun `shows TaskNotFoundException when use case throws`() {
//        every { reader.read() } returnsMany listOf("1", "", "", "X")
//        every {
//            updateUC.invoke(any(), any(), any(), any())
//        } throws TaskNotFoundException("notfound")
//
//        ui.run()
//
//        assertThat(printed.last()).contains("Task not founc")
//    }
//
//}
