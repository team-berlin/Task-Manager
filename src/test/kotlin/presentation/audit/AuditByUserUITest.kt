package presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.model.*
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.presentation.audit.AuditByUserUI
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditByUserUITest {

    private val viewer = mockk<Viewer>(relaxed = true)
    private val reader = mockk<Reader>()
    private val getAuditLogsByUserIdUseCase = mockk<GetAuditLogsByUserIdUseCase>()
    private val fetchAllUsers= mockk<FetchAllUsersUseCase>()
    private lateinit var ui: AuditByUserUI


    @BeforeEach
    fun setup() {

        ui = AuditByUserUI(getAuditLogsByUserIdUseCase,fetchAllUsers,viewer, reader)

        DummyData.users.clear()
        DummyData.users.addAll(
            listOf(User("U1", "alice", "secret",  UserRole.ADMIN))
        )
    }

    @Test
    fun `should display audit logs for selected user`() {
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns listOf(
            AuditLog(
                id = "A1",
                timestamp = 1111L,
                createdByUserId = "U1",
                auditAction = AuditAction.CREATE,
                changesDescription = "Initial setup",
                entityType = EntityType.PROJECT,
                entityId = "P1"
            )
        )

        ui.run()

        verify {
            viewer.show("=== Audit Logs by alice ===")
            viewer.show(
                match {
                    it.contains("ID: A1") &&
                            it.contains("Time: 1111") &&
                            it.contains("Action: CREATE") &&
                            it.contains("Entity ID: P1") &&
                            it.contains("Changes: Initial setup")
                }
            )
        }
    }

    @Test
    fun `should show 'null' for missing changes description`() {
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns listOf(
            AuditLog(
                id = "A2",
                timestamp = 2222L,
                createdByUserId = "U1",
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
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns emptyList()

        ui.run()

        verify {
            viewer.show("No audit logs found for user alice.")
        }
    }

    @Test
    fun `should handle InputCancelledException gracefully`() {
        every { reader.read() } returns "x"

        ui.run()

        verify { viewer.show("Cancelled.") }
    }

    @Test
    fun `should handle InvalidSelectionException gracefully`() {
        every { reader.read() } returns "invalid"

        ui.run()

        verify { viewer.show("Invalid selection") }
    }
}