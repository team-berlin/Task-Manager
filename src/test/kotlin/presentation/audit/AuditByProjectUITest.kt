package com.berlin.presentation.audit

import com.berlin.data.DummyData
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.usecase.audit_system.GetAuditLogsByProjectIdUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AuditByProjectUITest {

    private lateinit var getAuditLogsByProjectIdUseCase: GetAuditLogsByProjectIdUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var ui: AuditByProjectUI

    private val sampleProject = Project("P1", "Test Project", null, null, null)
    private val sampleLogs = listOf(
        AuditLog(
            id = "A1",
            timestamp = 1234567890L,
            createdByUserId = "U1",
            auditAction = AuditLog.AuditAction.CREATE,
            changesDescription = "Initial creation",
            entityType = AuditLog.EntityType.PROJECT,
            entityId = "P1"
        )
    )

    @BeforeEach
    fun setup() {
        getAuditLogsByProjectIdUseCase = mockk()
        getAllProjectsUseCase = mockk()
        viewer = mockk(relaxed = true)
        reader = mockk()
        ui = AuditByProjectUI(getAuditLogsByProjectIdUseCase, getAllProjectsUseCase, viewer, reader)

        DummyData.projects.clear()
        DummyData.projects.add(sampleProject)
    }

    @Test
    fun `displays audit logs for selected project`() {
        every { reader.read() } returns "1"
        every { getAuditLogsByProjectIdUseCase("P1") } returns sampleLogs
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        ui.run()

        verify {
            viewer.show(match { it.contains("=== Audit Logs for Test Project ===") })
            viewer.show(match { it.contains("ID: A1") })
            viewer.show(match { it.contains("Initial creation") })
        }
    }

    @Test
    fun `displays message when no logs exist for project`() {
        every { reader.read() } returns "1"
        every { getAuditLogsByProjectIdUseCase("P1") } returns emptyList()
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        ui.run()

        verify {
            viewer.show("No audit logs found for project Test Project.")
        }
    }

    @Test
    fun `isAllowed returns true when getAuditByProject is true`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByProject } returns true

        val result = ui.isAllowed(permission)

        assertThat(result).isTrue()
    }

    @Test
    fun `isAllowed returns false when getAuditByProject is false`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByProject } returns false

        val result = ui.isAllowed(permission)

        assertThat(result).isFalse()
    }

    @Test
    fun `displays cancelled message when input is x`() {
        every { reader.read() } returns "x"
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        ui.run()

        verify {
            viewer.show("Cancelled.")
        }
    }

    @Test
    fun `displays null when changesDescription is null`() {
        every { reader.read() } returns "1"
        every { getAuditLogsByProjectIdUseCase("P1") } returns listOf(
            AuditLog(
                id = "A2",
                timestamp = 1234567891L,
                createdByUserId = "U2",
                auditAction = AuditLog.AuditAction.UPDATE,
                changesDescription = null,
                entityType = AuditLog.EntityType.PROJECT,
                entityId = "P1"
            )
        )
        every { getAllProjectsUseCase() } returns listOf(sampleProject)

        ui.run()

        verify {
            viewer.show(match { it.contains("Changes: null") })
        }
    }

    @Test
    fun `displays invalid selection for non-number input`() {
        every { reader.read() } returns "not-a-number"
        every { getAllProjectsUseCase() } returns listOf(sampleProject)

        ui.run()

        verify {
            viewer.show("Invalid selection")
        }
    }

}

