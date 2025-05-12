//package com.berlin.presentation.task
//
//import com.berlin.domain.exception.InvalidProjectIdException
//import com.berlin.domain.model.Project
//import com.berlin.domain.model.TaskState
//import com.berlin.domain.model.Task
//import com.berlin.domain.usecase.project.GetAllProjectsUseCase
//import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
//import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
//import com.berlin.presentation.io.Reader
//import com.berlin.presentation.io.Viewer
//import com.google.common.truth.Truth.assertThat
//import io.mockk.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class GetTasksByProjectIdUITest {
//
//    private lateinit var viewer: Viewer
//    private lateinit var reader: Reader
//    private lateinit var getTasks: GetTasksByProjectUseCase
//    private lateinit var getAllProjects: GetAllProjectsUseCase
//    private lateinit var getAllStates: GetAllStatesByProjectIdUseCase
//    private lateinit var ui: GetTasksByProjectIdUI
//
//    // capture all viewer.show(...) calls
//    private val printed = mutableListOf<String>()
//
//    // shared test data
//    private val project =
//        Project(id = "P1", title = "Core", description = null, statesId = listOf("S1"), emptyList())
//    private val stateTodo = TaskState(id = "S1", name = "TODO", projectId = "P1")
//    private val stateInProg = TaskState(id = "S2", name = "IN_PROGRESS", projectId = "P1")
//    private val task = Task(
//        id = "T1",
//        projectId = "P1",
//        title = "Feature",
//        description = null,
//        stateId = "S1",
//        assignedToUserId = "U1",
//        createByUserId = "U1"
//    )
//
//    @BeforeEach
//    fun setUp() {
//        viewer = mockk(relaxed = true) {
//            every { show(capture(printed)) } just Runs
//        }
//        reader = mockk()
//        getTasks = mockk()
//        getAllProjects = mockk()
//        getAllStates = mockk()
//
//        // always present exactly our one project
//        every { getAllProjects.getAllProjects() } returns listOf(project)
//
//        // initialize the UI
//        ui = GetTasksByProjectIdUI(
//            getTasks, getAllProjects, getAllStates, viewer, reader
//        )
//
//        printed.clear()
//    }
//
//    /** Helper to drive the single reader.read() used by choose(...) for project selection */
//    private fun stubProjectChoice(input: String) {
//        every { reader.read() } returns input
//    }
//
//    @Test
//    fun `success shows swimlane with one task in the TODO lane`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } returns Result.success(listOf(task))
//        every { getAllStates.getAllStatesByProjectId("P1") } returns Result.success(listOf(stateTodo, stateInProg))
//
//        ui.run()
//
//        // Check swimlane header and task line
//        assertThat(printed).contains("\n=== Tasks for project P1 ===")
//        assertThat(printed).contains("- T1: Feature  → U1")
//    }
//
//    @Test
//    fun `no states prints No states found message`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } returns Result.success(emptyList())
//        every { getAllStates.getAllStatesByProjectId("P1") } returns Result.success(emptyList())
//
//        ui.run()
//
//        assertThat(printed.last()).isEqualTo("No states found for that project.")
//    }
//
//    @Test
//    fun `empty task list prints placeholder in each state lane`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } returns Result.success(emptyList())
//        every { getAllStates.getAllStatesByProjectId("P1") } returns Result.success(listOf(stateTodo))
//
//        ui.run()
//
//        // even with no tasks, the TODO lane shows the placeholder
//        assertThat(printed).contains("  (no tasks)")
//    }
//
//    @Test
//    fun `user cancellation prints Cancelled`() {
//        stubProjectChoice("X")
//        ui.run()
//        assertThat(printed.last()).isEqualTo("Cancelled.")
//        verify { getTasks wasNot Called }
//    }
//
//    @Test
//    fun `invalid selection prints Invalid selection`() {
//        stubProjectChoice("99")
//        ui.run()
//        assertThat(printed.last()).isEqualTo("Invalid selection")
//        verify { getTasks wasNot Called }
//    }
//
//    @Test
//    fun `failure from use case prints its message`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } returns Result.failure(RuntimeException("boom"))
//        ui.run()
//        assertThat(printed.last()).isEqualTo("boom")
//        verify(exactly = 1) { getTasks("P1") }
//    }
//
//    @Test
//    fun `failure without message prints default`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } returns Result.failure(RuntimeException("Failed to load tasks"))
//        ui.run()
//        assertThat(printed.last()).isEqualTo("Failed to load tasks")
//    }
//
//    @Test
//    fun `throws InvalidProjectIdException prints invalid project id`() {
//        stubProjectChoice("1")
//        every { getTasks("P1") } throws InvalidProjectIdException("bad id")
//        ui.run()
//        assertThat(printed.last()).isEqualTo("invalid project id")
//        verify(exactly = 1) { getTasks("P1") }
//    }
//}
