package com.berlin.data.audit

import com.berlin.data.BaseDataSource
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
    private val auditLogMapper: AuditLogMapper = mockk()

    @BeforeEach
    fun setUp() {
        repository = AuditRepositoryImpl(auditLogDataSource, auditLogMapper)
    }

    @Test
    fun `addAuditLog should succeed when write passes`() {
        every { auditLogMapper.mapToDataModel(validProjectAuditLog) } returns validProjectAuditLogDto
        every { auditLogDataSource.write(validProjectAuditLogDto) } returns true

        repository.addAuditLog(validProjectAuditLog)
    }

    @Test
    fun `addAuditLog should throw exception when write fails`() {
        every { auditLogMapper.mapToDataModel(validProjectAuditLog) } returns validProjectAuditLogDto
        every { auditLogDataSource.write(validProjectAuditLogDto) } returns false

        assertThrows<InvalidAuditLogException> {
            repository.addAuditLog(validProjectAuditLog)
        }
    }

    @Test
    fun `getAuditLogsByProjectId should return matching logs`() {
        every { auditLogDataSource.getAll() } returns listOf(validProjectAuditLogDto, validTaskAuditLogDto)
        every { auditLogMapper.mapToDomainModel(validProjectAuditLogDto) } returns validProjectAuditLog
        every { auditLogMapper.mapToDomainModel(validTaskAuditLogDto) } returns validTaskAuditLog

        val result = repository.getAuditLogsByProjectId("P1")

        assertThat(result).containsExactly(validProjectAuditLog)
    }

    @Test
    fun `getAuditLogsByTaskId should return matching logs`() {
        every { auditLogDataSource.getAll() } returns listOf(validProjectAuditLogDto, validTaskAuditLogDto)
        every { auditLogMapper.mapToDomainModel(validProjectAuditLogDto) } returns validProjectAuditLog
        every { auditLogMapper.mapToDomainModel(validTaskAuditLogDto) } returns validTaskAuditLog

        val result = repository.getAuditLogsByTaskId("T1")

        assertThat(result).containsExactly(validTaskAuditLog)
    }

    @Test
    fun `getAuditLogsByUserId should return matching logs`() {

        every { auditLogDataSource.getAll() } returns listOf(validTaskAuditLogDto)
        every { auditLogMapper.mapToDomainModel(validProjectAuditLogDto) } returns validProjectAuditLog
        every { auditLogMapper.mapToDomainModel(validTaskAuditLogDto) } returns validTaskAuditLog

        val result = repository.getAuditLogsByUserId("U1")

        assertThat(result).containsExactly(validTaskAuditLog)
    }


    val validProjectAuditLogDto = AuditLogDto(
        id = "log-1",
        timestamp = System.currentTimeMillis(),
        createdByUserId = "U1",
        auditAction = AuditLog.AuditAction.CREATE,
        changesDescription = "Created something",
        entityType = AuditLog.EntityType.PROJECT,
        entityId = "P1"
    )

    val validProjectAuditLog = AuditLog(
        id = "log-1",
        timestamp = System.currentTimeMillis(),
        createdByUserId = "U1",
        auditAction = AuditLog.AuditAction.CREATE,
        changesDescription = "Created something",
        entityType = AuditLog.EntityType.PROJECT,
        entityId = "P1"
    )

    val validTaskAuditLogDto = AuditLogDto(
        id = "log-2",
        timestamp = System.currentTimeMillis(),
        createdByUserId = "U1",
        auditAction = AuditLog.AuditAction.UPDATE,
        changesDescription = "Updated task",
        entityType = AuditLog.EntityType.TASK,
        entityId = "T1"
    )

    val validTaskAuditLog = AuditLog(
        id = "log-2",
        timestamp = System.currentTimeMillis(),
        createdByUserId = "U1",
        auditAction = AuditLog.AuditAction.UPDATE,
        changesDescription = "Updated task",
        entityType = AuditLog.EntityType.TASK,
        entityId = "T1"
    )

}

