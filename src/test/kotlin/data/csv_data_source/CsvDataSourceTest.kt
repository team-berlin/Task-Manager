package com.berlin.data.csv_data_source

import com.berlin.data.BaseSchema
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.model.Permission
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class CsvDataSourceTest {

    private lateinit var csvDataSource: CsvDataSource<User>
    private lateinit var mockSchema: BaseSchema<User>

    private val testUser = User(
        id = "u1",
        userName = "testUser",
        password = "password123",
        permission = Permission(),
        role = UserRole.MATE
    )

    private val testUsers = listOf(
        testUser,
        User("u2", "user2", "pass2", permission = Permission(), UserRole.ADMIN),
        User("u3", "user3", "pass3", permission = Permission(), UserRole.MATE)
    )

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        mockSchema = mockk(relaxed = true)

        // Set up mock schema behavior
        every { mockSchema.fileName } returns "users.csv"
        every { mockSchema.header } returns listOf("id", "userName", "password", "role")
        every { mockSchema.getId(any()) } answers {
            val user = firstArg<User>()
            user.id
        }

        csvDataSource = CsvDataSource(tempDir.toString(), mockSchema)
    }

    //region initialization tests

    @Test
    fun `constructor should properly initialize with valid parameters`() {
        // Given: valid parameters passed to constructor
        // When: constructor is called (in setup)
        // Then: object should be created successfully
        assertThat(csvDataSource).isNotNull()
    }

    //endregion

    //region getAll tests

    @Test
    fun `getAll should throw FileNotFoundException when file does not exist`() {
        // Given: CSV file does not exist

        // When // Then: getAll is called
        assertThrows<FileNotFoundException> { csvDataSource.getAll() }
    }

    @Test
    fun `getAll should return list of entities when file exists with valid data`() {
        // Given: CSV file exists with test data
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers

        // When: getAll is called
        val result = csvDataSource.getAll()

        // Then: list of all entities should be returned
        assertThat(result).hasSize(testUsers.size)
        verify(atLeast = 1) { mockSchema.fromRow(any()) }
    }

    @Test
    fun `getAll should filter out null entities from schema conversion`() {
        // Given: CSV file exists but some rows will convert to null entities
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returns testUser andThen null andThen testUsers[2]

        // When: getAll is called
        val result = csvDataSource.getAll()

        // Then: only valid entities should be returned
        assertThat(result).hasSize(2)
        assertThat(result).contains(testUser)
        assertThat(result).contains(testUsers[2])
    }

    //endregion

    //region getById tests

    @Test
    fun `getById should return null when file does not exist`() {
        // Given: CSV file does not exist

        // When: getById is called with an id
        val result = csvDataSource.getById("u1")

        // Then: null should be returned
        assertThat(result).isNull()
    }

    @Test
    fun `getById should return entity when file exists and id matches`() {
        // Given: CSV file exists with test data
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers
        every { mockSchema.getId(testUser) } returns testUser.id
        // When: getById is called with an existing id
        val result = csvDataSource.getById("u1")

        // Then: matching entity should be returned
        assertThat(result).isEqualTo(testUser)
    }

    @Test
    fun `getById should return null when file exists but id does not match`() {
        // Given: CSV file exists with test data
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers

        // When: getById is called with a non-existent id
        val result = csvDataSource.getById("nonexistent")

        // Then: null should be returned
        assertThat(result).isNull()
    }

    //endregion

    //region write tests

    @Test
    fun `write should create file and return true when writing to non-existent file`() {
        // Given: file does not exist and schema returns valid row for entity
        val csvFile = File(tempDir.toFile(), mockSchema.fileName)
        every { mockSchema.toRow(any()) } returns listOf("u1", "testUser", "password123", "MATE")

        // When: write is called with a valid entity
        val result = csvDataSource.write(testUser)

        // Then: file should be created and operation should succeed
        assertThat(result).isTrue()
        assertThat(csvFile.exists()).isTrue()
        verify { mockSchema.toRow(testUser) }
    }

    @Test
    fun `write should append to file and return true when writing to existing file`() {
        // Given: file exists with test data
        createCsvWithTestData()
        val newUser = User("u4", "user4", "pass4", permission = Permission(), UserRole.ADMIN)
        every { mockSchema.toRow(newUser) } returns listOf("u4", "user4", "pass4", "ADMIN")

        // When: write is called with a new entity
        val result = csvDataSource.write(newUser)

        // Then: entity should be appended and operation should succeed
        assertThat(result).isTrue()
        verify { mockSchema.toRow(newUser) }
    }

    @Test
    fun `write should return false when schema returns empty row`() {
        // Given: schema returns empty row for the entity
        every { mockSchema.toRow(any()) } returns emptyList()

        // When: write is called with an invalid entity
        val result = csvDataSource.write(testUser)

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    //endregion

    //region writeAll tests

    @Test
    fun `writeAll should create file and return true when writing to non-existent file`() {
        // Given: file does not exist and schema converts entities to rows properly
        val csvFile = File(tempDir.toFile(), mockSchema.fileName)
        every { mockSchema.toRow(any()) } answers {
            val user = firstArg<User>()
            listOf(user.id, user.userName, user.password, user.role.toString())
        }

        // When: writeAll is called with multiple entities
        val result = csvDataSource.writeAll(testUsers)

        // Then: file should be created and operation should succeed
        assertThat(result).isTrue()
        assertThat(csvFile.exists()).isTrue()
        verify(exactly = testUsers.size) { mockSchema.toRow(any()) }
    }

    @Test
    fun `writeAll should return false when list is empty`() {
        // Given: empty list of entities

        // When: writeAll is called with empty list
        val result = csvDataSource.writeAll(emptyList())

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    @Test
    fun `writeAll should filter out entities that produce empty rows`() {
        // Given: some entities produce empty rows
        every { mockSchema.toRow(testUsers[0]) } returns listOf("u1", "testUser", "password123", "MATE")
        every { mockSchema.toRow(testUsers[1]) } returns emptyList()
        every { mockSchema.toRow(testUsers[2]) } returns listOf("u3", "user3", "pass3", "MATE")

        // When: writeAll is called with mix of valid/invalid entities
        val result = csvDataSource.writeAll(testUsers)

        // Then: operation should succeed with valid entities only
        assertThat(result).isTrue()
        verify(exactly = testUsers.size) { mockSchema.toRow(any()) }
    }

    //endregion

    //region update tests

    @Test
    fun `update should return false when file does not exist`() {
        // Given: CSV file does not exist

        // When: update is called with an id and entity
        val result = csvDataSource.update("u1", testUser)

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    @Test
    fun `update should return false when entity id does not exist`() {
        // Given: CSV file exists but doesn't contain the entity with specified id
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers

        // When: update is called with non-existent id
        val result = csvDataSource.update("nonexistent", testUser)

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    @Test
    fun `update should return true when update is successful`() {
        // Given: CSV file exists with entity that matches the id
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers
        val updatedUser = testUser.copy(userName = "updatedName")
        every { mockSchema.toRow(updatedUser) } returns listOf("u1", "updatedName", "password123", "MATE")

        // When: update is called with existing id and modified entity
        val result = csvDataSource.update("u1", updatedUser)

        // Then: entity should be updated and operation should succeed
        assertThat(result).isTrue()
        verify { mockSchema.toRow(updatedUser) }
    }

    //endregion

    //region delete tests

    @Test
    fun `delete should return false when file does not exist`() {
        // Given: CSV file does not exist

        // When: delete is called with an id
        val result = csvDataSource.delete("u1")

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    @Test
    fun `delete should return false when entity id does not exist`() {
        // Given: CSV file exists but doesn't contain entity with specified id
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers

        // When: delete is called with non-existent id
        val result = csvDataSource.delete("nonexistent")

        // Then: operation should fail
        assertThat(result).isFalse()
    }

    @Test
    fun `delete should return true when delete is successful`() {
        // Given: CSV file exists with entity that matches the id
        createCsvWithTestData()
        every { mockSchema.fromRow(any()) } returnsMany testUsers
        every { mockSchema.toRow(any()) } answers {
            val user = firstArg<User>()
            listOf(user.id, user.userName, user.password, user.role.toString())
        }

        // When: delete is called with existing id
        val result = csvDataSource.delete("u1")

        // Then: entity should be deleted and operation should succeed
        assertThat(result).isTrue()
    }

    //endregion

    private fun createCsvWithTestData() {
        val csvFile = File(tempDir.toFile(), mockSchema.fileName)
        csvFile.createNewFile()
        csvFile.writeText(
            mockSchema.header.joinToString(",") + "\n" +
                    "u1,testUser,password123,MATE\n" +
                    "u2,user2,pass2,ADMIN\n" +
                    "u3,user3,pass3,MATE\n"
        )
    }
}