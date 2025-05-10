package presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.model.*
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByTaskIdUseCase
import com.berlin.presentation.audit.AuditByTaskUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditByTaskUITest {

    private val viewer = mockk<Viewer>(relaxed = true)
    private val reader = mockk<Reader>()
    private val getAuditLogsByTaskIdUseCase = mockk<GetAuditLogsByTaskIdUseCase>()

    private lateinit var ui: AuditByTaskUI

    @BeforeEach
    fun setup() {

        ui = AuditByTaskUI(viewer, reader, getAuditLogsByTaskIdUseCase)

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
    fun `should display audit logs for selected task`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "1")
        coEvery { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns listOf(
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
    fun `should display null for missing changes description`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "1")
        coEvery { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns listOf(
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
    fun `should display message when no logs are found`() = runTest {
        coEvery { reader.read() } returnsMany listOf("1", "1")
        coEvery { getAuditLogsByTaskIdUseCase.getAuditLogsByTaskId("T1") } returns emptyList()

        ui.run()

        verify { viewer.show("No audit logs found for this task.") }
    }

    @Test
    fun `should handle InputCancelledException gracefully`() = runTest {
        coEvery { reader.read() } returns "x"

        ui.run()

        verify { viewer.show("Cancelled.") }
    }

    @Test
    fun `should handle InvalidSelectionException when user input is invalid`() = runTest {
        coEvery { reader.read() } returns "invalid"

        ui.run()

        verify { viewer.show("Invalid selection") }
    }
}