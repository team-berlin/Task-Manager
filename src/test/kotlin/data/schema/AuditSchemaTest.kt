package com.berlin.data.schema

import com.berlin.data.csv_data_source.schema.AuditSchema
import com.berlin.data.dto.AuditLogDto
import com.berlin.domain.model.AuditAction
import com.berlin.domain.model.AuditLog
import com.berlin.domain.model.EntityType
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuditSchemaTest {

    private lateinit var auditSchema: AuditSchema

    @BeforeEach
    fun setup() {
        auditSchema = fakeAuditSchema()
    }

    //region create object

    @Test
    fun `should throw IllegalArgumentException when try to create object with blank file name`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            auditSchema = AuditSchema("", listOf("a", "b", "c", "d", "e", "f", "g"))
        }
    }

    @Test
    fun `should throw IllegalArgumentException when try to create object with invalid size header`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            auditSchema = AuditSchema("test.csv", listOf("a", "b"))
        }
    }

    //endregion

    //region toRow

    @Test
    fun `toRow should return list of valid audit log attributes when valid audit log passed`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEqualTo(validRow)
    }

    @Test
    fun `toRow should return list of valid audit log attributes when audit log with empty changesDescription passed`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEqualTo(validRowEmptyChangesDescription)
    }

    @Test
    fun `toRow should return empty list when invalid audit log passed miss id attribute`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid audit log passed miss timestamp attribute`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid audit log passed miss createdBy attribute`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid audit log passed miss entityId attribute`() {
        //when
        val result = auditSchema.toRow(validAuditLogDto)
        //then
        assertThat(result).isEmpty()
    }

    //endregion

    //region fromRow

    @Test
    fun `fromRow should return audit log when valid row task full passed`() {
        //when
        val result = auditSchema.fromRow(validRow)
        //then
        assertThat(result).isEqualTo(validAuditLog)
    }

    @Test
    fun `fromRow should return audit log when valid row project full passed`() {
        //when
        val result = auditSchema.fromRow(validRowProject)
        //then
        assertThat(result).isEqualTo(validAuditLogProject)
    }

    @Test
    fun `fromRow should return audit log when valid row empty changesDescription passed`() {
        //when
        val result = auditSchema.fromRow(validRowEmptyChangesDescription)
        //then
        assertThat(result).isEqualTo(validAuditLogEmptyChangesDescription)
    }

    @Test
    fun `fromRow should return null when invalid row passed miss id column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss timestamp column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyTimestamp)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss createdBy column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyCreatedBy)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss auditAction column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyAuditAction)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed wrong auditAction column`() {
        //when
        val result = auditSchema.fromRow(invalidRowWrongAuditAction)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss entityType column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyEntityType)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid row passed miss entityId column`() {
        //when
        val result = auditSchema.fromRow(invalidRowEmptyEntityId)
        //then
        assertThat(result).isNull()
    }

    //endregion

    //region getId

    @Test
    fun `getId should return id of audit log passed`() {
        //when
        val result = auditSchema.getId(validAuditLogDto)
        //then
        assertThat(result).isEqualTo(validAuditLog.id)
    }

    @Test
    fun `getId should return null when audit log passed have empty id`() {
        //when
        val result = auditSchema.getId(validAuditLogDto)
        //then
        assertThat(result).isNull()
    }

    //endregion

    private fun fakeAuditSchema() = AuditSchema("test.csv", listOf("a", "b", "c", "d", "e", "f", "g"))

    private companion object {

        val testUserId = "u1"

        val validAuditLogDto = AuditLogDto(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = "e1"
        )

        //region Some AuditLogs

        val validAuditLog = AuditLog(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = "e1"
        )
        val validAuditLogProject = AuditLog(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.DELETE,
            changesDescription = "create",
            entityType = EntityType.PROJECT,
            entityId = "e1"
        )
        val validAuditLogEmptyChangesDescription = AuditLog(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = null,
            entityType = EntityType.TASK,
            entityId = "e1"
        )
        val invalidAuditLogEmptyId = AuditLog(
            id = "",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = "e1"
        )
        val invalidAuditLogZeroTimestamp = AuditLog(
            id = "a1",
            timestamp = 0L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = "e1"
        )
        val invalidAuditLogEmptyCreatedBy = AuditLog(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = "",
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = "e1"
        )
        val invalidAuditLogEmptyEntityId = AuditLog(
            id = "a1",
            timestamp = 1000L,
            createdByUserId = testUserId,
            auditAction = AuditAction.CREATE,
            changesDescription = "create",
            entityType = EntityType.TASK,
            entityId = ""
        )

        //endregion

        //region Some Rows

        val validRow = listOf(
            "a1", "1000", "u1", "CREATE", "create", "TASK", "e1"
        )
        val validRowProject = listOf(
            "a1", "1000", "u1", "DELETE", "create", "PROJECT", "e1"
        )
        val validRowEmptyChangesDescription = listOf(
            "a1", "1000", "u1", "CREATE", "", "TASK", "e1"
        )
        val invalidRowEmptyId = listOf(
            "", "1000", "u1", "CREATE", "create", "TASK", "e1"
        )
        val invalidRowEmptyTimestamp = listOf(
            "a1", "", "u1", "CREATE", "create", "TASK", "e1"
        )
        val invalidRowEmptyCreatedBy = listOf(
            "a1", "1000", "", "CREATE", "create", "TASK", "e1"
        )
        val invalidRowEmptyAuditAction = listOf(
            "a1", "1000", "u1", "", "create", "TASK", "e1"
        )
        val invalidRowWrongAuditAction = listOf(
            "a1", "1000", "u1", "jgj444h", "create", "TASK", "e1"
        )
        val invalidRowEmptyEntityType = listOf(
            "a1", "1000", "u1", "CREATE", "create", "", "e1"
        )
        val invalidRowEmptyEntityId = listOf(
            "a1", "1000", "u1", "CREATE", "create", "TASK", ""
        )

        //endregion
    }
}