package com.berlin.data

import com.berlin.model.Project
import com.google.common.truth.Truth.assertThat
import com.opencsv.CSVWriter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import kotlin.test.Test


class CsvDataSourceTest {

    private lateinit var dataSource: CsvDataSource<Project>
    private lateinit var testFile: File


    @BeforeEach
    fun setUp() {
        testFile = File("test_projects.csv")
        dataSource = createDataSource(testFile.path)
    }

    @AfterEach
    fun clear() {
        if (testFile.exists()) testFile.delete()
    }

    //region readAll test
    @Test
    fun `readAll should return throw exception when file is not exist`() {
        // Given
        dataSource = createDataSource("path")
        // When // then
        assertThrows<FileNotFoundException> { dataSource.readAll() }
    }

    @Test
    fun `readAll should return list of projects when valid file`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
            )
        )
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `readAll should return empty list when file is empty`() {
        // Given
        writeInCsv(testFile, emptyList())
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `readAll should return empty list when file is only have invalid row`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Description1"),
            )
        )
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `readAll should return empty list when file is only have the header row`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId")
            )
        )
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `readAll should return valid rows when file is have valid rows`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("165", "Project31", "Description6471", "[state17,state44,state28]", "[task91,task20,task70]")
            )
        )
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `readAll should return empty list when file have only spaces`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("", "", "", "", ""),
                listOf("", "", "", "", ""),
                listOf("", "", "", "", "")
            )
        )
        // When
        val result = dataSource.readAll()
        // Then
        assertThat(result).isEmpty()
    }
    //endregion

    private fun writeInCsv(file: File, rows: List<List<String>>) {
        FileWriter(file).use { writer ->
            CSVWriter(writer).use { csvWriter ->
                rows.forEach { row -> csvWriter.writeNext(row.toTypedArray()) }
            }
        }
    }

    private fun testFromRowMethodForProject(row:List<String>):Project?{
       return row.takeIf { row.size == 5 && row.all { cell -> cell.isNotBlank() }
        }?.let {
            Project(
                id = row[0],
                name = row[1],
                description = row[2],
                statesId = row[3]
                        .removeSurrounding("[", "]")
                        .split(","),
                tasksId = row[4]
                        .removeSurrounding("[", "]")
                        .split(",")
                        )
        }
    }

    private fun createDataSource(filePath: String): CsvDataSource<Project> {
        return object : CsvDataSource<Project>(
            filePath = filePath,
            headers = listOf("id", "name", "description", "statesId", "tasksId"),
            toRow = { emptyList() },
            fromRow = ::testFromRowMethodForProject
        ) {}
    }

}