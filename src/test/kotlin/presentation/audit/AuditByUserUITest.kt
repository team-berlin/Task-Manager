package com.berlin.presentation.audit

import com.berlin.domain.exception.InvalidUserIdException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.audit_system.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.google.common.truth.Truth.assertThat

class AuditByUserUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getAuditLogsByUserIdUseCase: GetAuditLogsByUserIdUseCase
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase
    private lateinit var ui: AuditByUserUI

    @BeforeEach
    fun setup() {
        viewer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        getAuditLogsByUserIdUseCase = mockk(relaxed = true)
        getAllUsersUseCase = mockk(relaxed = true)

        ui = AuditByUserUI(getAuditLogsByUserIdUseCase, getAllUsersUseCase, viewer, reader)
    }

    @Test
    fun `should display audit logs for selected user`() {
        val user = mockUser("U1", "alice")
        val auditLogs = listOf(mockAuditLog("A1", "U1"))

        every { getAllUsersUseCase() } returns listOf(user)
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase("U1") } returns auditLogs

        ui.run()

        verify { viewer.show("=== Audit Logs by alice ===") }
        verify { viewer.show(match { it.contains("ID: A1") }) }
    }

    @Test
    fun `should handle no audit logs found`() {
        val user = mockUser("U1", "alice")

        every { getAllUsersUseCase() } returns listOf(user)
        every { reader.read() } returns "1"
        every { getAuditLogsByUserIdUseCase("U1") } returns emptyList()

        ui.run()

        verify { viewer.show("No audit logs found for user alice.") }
    }

    @Test
    fun `displays cancelled message when input is x`() {
        every { reader.read() } returns "x"
        every { getAllUsersUseCase() } returns listOf(mockUser())
        ui.run()

        verify {
            viewer.show("Cancelled.")
        }
    }

    @Test
    fun `should handle invalid selection`() {
        every { getAllUsersUseCase() } returns emptyList()
        every { reader.read() } returns "invalid"

        ui.run()

        verify { viewer.show("Invalid selection") }
    }

    @Test
    fun `isAllowed returns true when getAuditByUser is true`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByUser } returns true

        val result = ui.isAllowed(permission)

        assertThat(result).isTrue()
    }

    @Test
    fun `isAllowed returns false when getAuditByUser is false`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByUser } returns false

        val result = ui.isAllowed(permission)

        assertThat(result).isFalse()
    }

    private fun mockUser(id: String = "U1", name: String = "alice") =
        User(id, name, User.UserRole.ADMIN)

    private fun mockAuditLog(id: String, userId: String) = AuditLog(
        id, 1111L, userId, AuditLog.AuditAction.CREATE,
        "Initial setup", AuditLog.EntityType.PROJECT, "P1"
    )
}
