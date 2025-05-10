package com.berlin.presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.model.*
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditByTaskUITest {

    private val viewer = mockk<Viewer>(relaxed = true)
    private val reader = mockk<Reader>()
    private lateinit var fetchAllUsersUseCase: FetchAllUsersUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getTasksByProjectUseCase: GetTasksByProjectUseCase
    private val getAuditLogsByTaskIdUseCase = mockk<GetAuditLogsByTaskIdUseCase>()

    private lateinit var ui: AuditByTaskUI

    @BeforeEach
    fun setup() {
        fetchAllUsersUseCase = mockk()
        getAllProjectsUseCase = mockk()
        getTasksByProjectUseCase = mockk()
        ui = AuditByTaskUI(viewer, reader, getAuditLogsByTaskIdUseCase, getTasksByProjectUseCase, getAllProjectsUseCase)

        every { getAllProjectsUseCase.getAllProjects() }returns listOf(
            Project("P1", "Project 1", null, emptyList(), emptyList())
        )
        every { getTasksByProjectUseCase.invoke("P1") }returns Result.success(listOf(
            Task("T1", "P1", "Task 1", null, "S1", "U2", "U1")
        ))

        DummyData.projects.clear()
        DummyData.projects.addAll(
            listOf(
                Project("P1", "Project 1", null, emptyList(), emptyList())
            )
        )
        DummyData.initialDemoTasks.clear()
        DummyData.initialDemoTasks.addAll(
            listOf(
                Task("T1", "P1", "Task 1", null, "S1", "U2", "U1")
            )
        )
    }

    @Test
    fun `should display audit logs for selected task`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns listOf(
            AuditLog(
                id = "A1",
                timestamp = 111,
                createdByUserId = "U1",
                auditAction = AuditAction.UPDATE,
                changesDescription = "Changed state",
                entityType = EntityType.TASK,
                entityId = "T1"
            )
        )

        ui.run()

        verify {
            viewer.show("=== Audit Logs for Task ===")
            viewer.show(
                match {
                    it.contains("ID: A1") &&
                            it.contains("Time: 111") &&
                            it.contains("By: U1") &&
                            it.contains("Action: UPDATE") &&
                            it.contains("Entity ID: T1") &&
                            it.contains("Changes: Changed state")
                }
            )
        }
    }

    @Test
    fun `should display null for missing changes description`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns listOf(
            AuditLog(
                id = "A2",
                timestamp = 999,
                createdByUserId = "U2",
                auditAction = AuditAction.DELETE,
                changesDescription = null,
                entityType = EntityType.TASK,
                entityId = "T1"
            )
        )

        ui.run()

        verify {
            viewer.show(match { it.contains("Changes: null") })
        }
    }

    @Test
    fun `should display message when no logs are found`() {
        every { reader.read() } returnsMany listOf("1", "1")
        every { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns emptyList()
        ui.run()

        verify { viewer.show("No audit logs found for this task.") }
    }

    @Test
    fun `should handle InputCancelledException gracefully`() {
        every { reader.read() } returns "x"
        ui.run()

        verify { viewer.show("Cancelled.") }
    }

    @Test
    fun `should handle InvalidSelectionException when user input is invalid`() {
        every { reader.read() } returns "invalid"

        ui.run()

        verify { viewer.show("Invalid selection") }
    }
    @Test
    fun `should handle out-of-range index for task selection`() {
        every { reader.read() } returnsMany listOf("1", "99") // Only 1 task available

        ui.run()

        verify { viewer.show("Invalid selection") }
    }

    @Test
    fun `should handle out-of-range index for user selection`() {
        every { reader.read() } returns "5" // Only 1 user available

        ui.run()

        verify { viewer.show("Invalid selection") }
    }
    @Test
    fun `should allow permission if getAuditByTask is true`() {
        val permission = mockk<Permission>()
        every { permission.getAuditByTask } returns true

        val ui = AuditByTaskUI(
            viewer = viewer,
            reader = reader,
            getAuditLogsByTaskIdUseCase = getAuditLogsByTaskIdUseCase,
            getTasksByProjectUseCase = getTasksByProjectUseCase,
            getAllProjectsUseCase = getAllProjectsUseCase
        )

        val result = ui.isAllowed(permission)

        assertTrue(result)
    }

    @Test
    fun `should deny permission if getAuditByTask is false`() {
        val permission = mockk<Permission>()
        every { permission.getAuditByTask } returns false

        val ui = AuditByTaskUI(
            viewer = viewer,
            reader = reader,
            getAuditLogsByTaskIdUseCase = getAuditLogsByTaskIdUseCase,
            getTasksByProjectUseCase = getTasksByProjectUseCase,
            getAllProjectsUseCase = getAllProjectsUseCase
        )

        val result = ui.isAllowed(permission)

        assertFalse(result)
    }

    @Test
    fun `should list tasks when getTasksByProjectUseCase returns a non-empty list`() {
        // Arrange
        val project = Project("P1", "Project 1", null, emptyList(), emptyList())
        val task = Task(
            id = "T1",
            projectId = "P1",
            title = "Task 1",
            description = "desc",
            stateId = "S1",
            assignedToUserId = "U1",
            createByUserId = "U1"
        )

        every { getAllProjectsUseCase.getAllProjects() } returns listOf(project)
        every { reader.read() } returnsMany listOf("1", "1") // Select project, then task
        every { getTasksByProjectUseCase("P1") } returns Result.success(listOf(task))
        every { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns emptyList()

        val ui = AuditByTaskUI(viewer, reader, getAuditLogsByTaskIdUseCase, getTasksByProjectUseCase, getAllProjectsUseCase)

        // Act
        ui.run()

        // Assert
        verify { getTasksByProjectUseCase("P1") }
        verify { viewer.show("No audit logs found for this task.") }
    }
    @Test
    fun `should handle failure from getTasksByProjectUseCase by falling back to emptyList`() {
        // Arrange
        val project = Project("P1", "Project 1", null, emptyList(), emptyList())

        every { getAllProjectsUseCase.getAllProjects() } returns listOf(project)
        every { reader.read() } returns "1" // Select project
        every { getTasksByProjectUseCase("P1") } returns Result.failure(RuntimeException("DB error"))

        val ui = AuditByTaskUI(viewer, reader, getAuditLogsByTaskIdUseCase, getTasksByProjectUseCase, getAllProjectsUseCase)

        // Act
        ui.run()

        // Assert
        verify { viewer.show("Invalid selection") } // because user sees empty list and input is invalid
    }





}