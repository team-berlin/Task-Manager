package com.berlin.presentation.audit

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.Permission
import com.berlin.domain.model.Project
import com.berlin.domain.model.Task
import com.berlin.domain.usecase.audit_system.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.project.GetAllProjectsUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditByTaskUITest {

    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var getAuditLogsByTaskIdUseCase: GetAuditLogsByTaskIdUseCase
    private lateinit var getTasksByProjectUseCase: GetTasksByProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var auditByTaskUI: AuditByTaskUI


    private val sampleProject = Project(
        "P1", "Test Project", null, null, null
    )

    private val sampleLogs = listOf(
        AuditLog(
            id = "A1",
            timestamp = 1234567890L,
            createdByUserId = "U1",
            auditAction = AuditLog.AuditAction.CREATE,
            changesDescription = "Initial creation",
            entityType = AuditLog.EntityType.TASK,
            entityId = "T1"
        )
    )

    @BeforeEach
    fun setUp() {
        viewer = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        getAuditLogsByTaskIdUseCase = mockk(relaxed = true)
        getTasksByProjectUseCase = mockk(relaxed = true)
        getAllProjectsUseCase = mockk(relaxed = true)

        auditByTaskUI =
            AuditByTaskUI(viewer, reader, getAuditLogsByTaskIdUseCase, getTasksByProjectUseCase, getAllProjectsUseCase)
    }

    @Test
    fun `displays audit logs for selected task`() {

        every { reader.read() } returns "1"
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        every { getAuditLogsByTaskIdUseCase("T1") } returns sampleLogs

        auditByTaskUI.run()

    }


    @Test
    fun `should return true when permission allows audit access`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByTask } returns true

        assertThat(auditByTaskUI.isAllowed(permission)).isTrue()
    }

    @Test
    fun `displays cancelled message when input is x`() {
        every { getAllProjectsUseCase() } throws InputCancelledException("Cancelled.")
        every { reader.read() } returns "x"
        auditByTaskUI.run()
        verify { viewer.show("Cancelled.") }
    }

    @Test
    fun `displays invalid selection for non-number input`() {
        every { reader.read() } returns "not-a-number"
        every { getAllProjectsUseCase() } throws InvalidSelectionException("Invalid selection")

        auditByTaskUI.run()

        verify {
            viewer.show("Invalid selection")
        }
    }

    @Test
    fun `isAllowed returns true when getAuditByTask is true`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByTask } returns true

        val result = auditByTaskUI.isAllowed(permission)

        assertThat(result).isTrue()
    }

    @Test
    fun `isAllowed returns false when getAuditByTask is false`() {
        val permission = mockk<Permission>(relaxed = true)
        every { permission.getAuditByTask } returns false

        val result = auditByTaskUI.isAllowed(permission)

        assertThat(result).isFalse()
    }

}
