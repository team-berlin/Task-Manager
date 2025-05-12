package com.berlin.data.audit

import com.berlin.data.BaseDataSource
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.dto.AuditLogDto
import com.berlin.data.mapper.AuditLogMapper
import com.berlin.domain.exception.InvalidAuditLogException
import com.berlin.domain.model.AuditLog
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class AuditRepositoryImplTest {

    private lateinit var repository: AuditRepositoryImpl
    private val auditLogDataSource: BaseDataSource<AuditLogDto> = mockk()

    @BeforeEach
    fun setUp() {
        val auditLogMapper: AuditLogMapper = mockk()
        repository = AuditRepositoryImpl(auditLogDataSource, auditLogMapper)
    }

    @Test
    fun `addAuditLog should return success when write succeeds`() {
        //Given
        every { auditLogDataSource.write(any()) } returns true

        //When
        val result = repository.addAuditLog(validAuditLog)

        //Then
        assertThat(result).isEqualTo(validAuditLog.id)
    }

    @Test
    fun `addAuditLog should return failure when write fails`() {
        //Given
        every { auditLogDataSource.write(any()) } returns false

        //When
        val result = repository.addAuditLog(validAuditLog)

        //Then
        assertThrows<InvalidAuditLogException> {
            repository.addAuditLog(validAuditLog)
        }
        assertThat(result).isEqualTo("fail to add audit log")
    }

    @Test
    fun `getAuditLogsByProjectId should return only logs with matching project id`() {
        //Given
        every { auditLogDataSource.getAll() } returns auditLogs

        //When
        val result = repository.getAuditLogsByProjectId("project-1")

        //Then
        assertThat(result).containsExactly(auditLogs[0])
    }

    @Test
    fun `getAuditLogsByTaskId should return only logs with matching task id`() {
        //Given
        every { auditLogDataSource.getAll() } returns auditLogs

        //When
        val result = repository.getAuditLogsByTaskId("task-1")

        //Then
        assertThat(result).containsExactly(auditLogs[1])
    }

    @Test
    fun `getAuditLogsByUserId should return only logs created by given user`() {
        //Given
        every { auditLogDataSource.getAll() } returns auditLogs

        //When
        val result = repository.getAuditLogsByUserId("user-1")

        //Then
        assertThat(result).containsExactly(auditLogs[0], auditLogs[1])
    }

    companion object {
        private val validAuditLog = AuditLog(
            id = "log-1",
            timestamp = System.currentTimeMillis(),
            createdByUserId = "user-1",
            auditAction = AuditLog.AuditAction.CREATE,
            changesDescription = "Created something",
            entityType = AuditLog.EntityType.PROJECT,
            entityId = "project-1"
        )

        private val auditLogs = listOf(
            AuditLogDto(
                id = "log-1",
                timestamp = 1000L,
                createdByUserId = "user-1",
                auditAction = AuditLog.AuditAction.CREATE,
                changesDescription = "Created project",
                entityType = AuditLog.EntityType.PROJECT,
                entityId = "project-1"
            ),
            AuditLogDto(
                id = "log-2",
                timestamp = 2000L,
                createdByUserId = "user-1",
                auditAction = AuditLog.AuditAction.UPDATE,
                changesDescription = "Updated task",
                entityType = AuditLog.EntityType.TASK,
                entityId = "task-1"
            ),
            AuditLogDto(
                id = "log-3",
                timestamp = 3000L,
                createdByUserId = "user-2",
                auditAction = AuditLog.AuditAction.DELETE,
                changesDescription = "Deleted something",
                entityType = AuditLog.EntityType.PROJECT,
                entityId = "project-2"
            )
        )
    }
}