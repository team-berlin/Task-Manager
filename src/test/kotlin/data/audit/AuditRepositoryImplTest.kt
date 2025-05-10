package data.audit

import com.berlin.data.audit.AuditRepositoryImpl
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.domain.exception.InvalidAuditLogException
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AuditRepositoryImplTest {

    private lateinit var repository: AuditRepositoryImpl
    private val csvDataSource: CsvDataSource<AuditLog> = mockk()

    @BeforeEach
    fun setUp() {
        repository = AuditRepositoryImpl(csvDataSource)
    }

    @Test
    fun `addAuditLog should return success when write succeeds`() = runTest {
        //Given
        coEvery { csvDataSource.write(any()) } returns true

        //When
        val result = repository.addAuditLog(validAuditLog)

        //Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(validAuditLog.id)
    }

    @Test
    fun `addAuditLog should return failure when write fails`() = runTest {
        //Given
        coEvery { csvDataSource.write(any()) } returns false

        //When
        val result = repository.addAuditLog(validAuditLog)

        //Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidAuditLogException::class.java)
    }

    @Test
    fun `getAuditLogsByProjectId should return only logs with matching project id`() = runTest {
        //Given
        coEvery { csvDataSource.getAll() } returns auditLogs

        //When
        val result = repository.getAuditLogsByProjectId("project-1")

        //Then
        assertThat(result).containsExactly(auditLogs[0])
    }

    @Test
    fun `getAuditLogsByTaskId should return only logs with matching task id`() = runTest {
        //Given
        coEvery { csvDataSource.getAll() } returns auditLogs

        //When
        val result = repository.getAuditLogsByTaskId("task-1")

        //Then
        assertThat(result).containsExactly(auditLogs[1])
    }

    @Test
    fun `getAuditLogsByUserId should return only logs created by given user`() = runTest {
        //Given
        coEvery { csvDataSource.getAll() } returns auditLogs

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
            auditAction = AuditAction.CREATE,
            changesDescription = "Created something",
            entityType = EntityType.PROJECT,
            entityId = "project-1"
        )

        private val auditLogs = listOf(
            AuditLog(
                id = "log-1",
                timestamp = 1000L,
                createdByUserId = "user-1",
                auditAction = AuditAction.CREATE,
                changesDescription = "Created project",
                entityType = EntityType.PROJECT,
                entityId = "project-1"
            ),
            AuditLog(
                id = "log-2",
                timestamp = 2000L,
                createdByUserId = "user-1",
                auditAction = AuditAction.UPDATE,
                changesDescription = "Updated task",
                entityType = EntityType.TASK,
                entityId = "task-1"
            ),
            AuditLog(
                id = "log-3",
                timestamp = 3000L,
                createdByUserId = "user-2",
                auditAction = AuditAction.DELETE,
                changesDescription = "Deleted something",
                entityType = EntityType.PROJECT,
                entityId = "project-2"
            )
        )
    }
}