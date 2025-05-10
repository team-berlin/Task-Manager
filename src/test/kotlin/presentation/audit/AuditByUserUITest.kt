package com.berlin.presentation.audit

import com.berlin.domain.model.*
import com.berlin.domain.usecase.audit_system.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
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
    private val getAllUsersUseCase = mockk<GetAllUsersUseCase>()
    private lateinit var ui: AuditByUserUI

    private val testUser = User("U1", "alice", "secret", UserRole.ADMIN)

    @BeforeEach
    fun setup() {
        ui = AuditByUserUI(getAuditLogsByUserIdUseCase, getAllUsersUseCase, viewer, reader)
    }

    @Test
    fun `should display audit logs for selected user`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns listOf(
            AuditLog("A1", 1111L, "U1", AuditAction.CREATE, "Initial setup", EntityType.PROJECT, "P1")
        )

        ui.run()

        verify {
            viewer.show("=== Audit Logs by alice ===")
            viewer.show(match {
                it.contains("ID: A1") &&
                        it.contains("Time: 1111") &&
                        it.contains("Action: CREATE") &&
                        it.contains("Entity ID: P1") &&
                        it.contains("Changes: Initial setup")
            })
        }
    }

    @Test
    fun `should show 'null' for missing changes description`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns listOf(
            AuditLog("A2", 2222L, "U1", AuditAction.DELETE, null, EntityType.TASK, "T1")
        )

        ui.run()

        verify { viewer.show(match { it.contains("Changes: null") }) }
    }

    @Test
    fun `should display message when no logs are found`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns emptyList()

        ui.run()

        verify { viewer.show("No audit logs found for user alice.") }
    }

    @Test
    fun `should handle InputCancelledException gracefully`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "x"

        ui.run()

        verify { viewer.show("Cancelled.") }
    }

    @Test
    fun `should handle InvalidSelectionException gracefully`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "invalid"

        ui.run()

        verify { viewer.show("Invalid selection") }
    }

    @Test
    fun `should handle failure result from fetchAllUsersUseCase`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.failure(Exception("DB error"))
        every { reader.read() } returns "1"

        ui.run()

        verify { viewer.show("Invalid selection") } // because list is empty → InvalidSelectionException
    }

    @Test
    fun `should handle empty user list from fetchAllUsersUseCase`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(emptyList())
        every { reader.read() } returns "1"

        ui.run()

        verify { viewer.show("Invalid selection") } // because there’s nothing to choose
    }

    @Test
    fun `should sort logs by timestamp ascending before display`() {
        every { getAllUsersUseCase.getAllUsers() } returns Result.success(listOf(testUser))
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase.getAuditLogsByUserId("U1") } returns listOf(
            AuditLog("A3", 3000L, "U1", AuditAction.UPDATE, "Updated project", EntityType.PROJECT, "P2"),
            AuditLog("A1", 1000L, "U1", AuditAction.CREATE, "Created project", EntityType.PROJECT, "P1")
        )

        ui.run()

        verify {
            viewer.show("=== Audit Logs by alice ===")
            viewer.show(match { it.contains("ID: A1") }) // first log by timestamp
            viewer.show(match { it.contains("ID: A3") }) // second
        }
    }
}
