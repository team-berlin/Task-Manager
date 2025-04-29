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
    fun `readAll should return throw FileNotFoundException when file is not exist`() {
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
    // region update test
    @Test
    fun `update should throw FileNotFoundException when file does not exist`() {
        // Given
        dataSource = createDataSource("path")
        val updatedProject = Project(
            id = "1",
            name = "UpdatedProject",
            description = "UpdatedDescription",
            statesId = listOf("state4", "state5"),
            tasksId = listOf("task4")
        )
        // When // Then
        assertThrows<FileNotFoundException> { dataSource.update("1", updatedProject) }
    }

    @Test
    fun `update should replace project with matching id when valid file`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll().find { it.id == "1" }).isEqualTo(UPDATED_PROJECT)
    }

    @Test
    fun `update should keep other row unchanged when replacing row with matching id`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll().find { it.id == "2" }?.name).isEqualTo("Project2")
    }

    @Test
    fun `update should do nothing when id does not exist`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        dataSource.update("3", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll().find { it.id == "3" }).isNull()
    }

    @Test
    fun `update should return false id does not exist`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        val result=dataSource.update("3", UPDATED_PROJECT)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `update should do nothing when file is empty`() {
        // Given
        writeInCsv(testFile, emptyList())
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll()).isEmpty()
    }

    @Test
    fun `update should return false when file is empty`() {
        // Given
        writeInCsv(testFile, emptyList())
        // When
        val result=dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `update should do nothing when file has only header row`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId")
            )
        )
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll()).isEmpty()
    }

    @Test
    fun `update should return false when file has only header row`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId")
            )
        )
        // When
        val result=dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `update should replace project with matching id when multiple valid rows`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project123", "Description16", "[state1,state23]", "[task133,task352]"),
                listOf("165", "Project31", "Description6471", "[state17,state44,state28]", "[task91,task20,task70]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll().find { it.id == "1" }).isEqualTo(UPDATED_PROJECT)
    }

    @Test
    fun `update should return true when when multiple valid rows`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project123", "Description16", "[state1,state23]", "[task133,task352]"),
                listOf("165", "Project31", "Description6471", "[state17,state44,state28]", "[task91,task20,task70]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        val result=dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `update should keep other rows unchanged when multiple valid rows`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("id", "name", "description", "statesId", "tasksId"),
                listOf("1", "Project1", "Description1", "[state1,state2]", "[task1,task2]"),
                listOf("165", "Project31", "Description6471", "[state17,state44,state28]", "[task91,task20,task70]"),
                listOf("2", "Project2", "Description2", "[state3]", "[task3]")
            )
        )
        // When
        dataSource.update("165", UPDATED_PROJECT)

        // Then
        assertThat(dataSource.readAll().find { it.id == "1" }?.name).isEqualTo("Project1")
    }

    @Test
    fun `update should do nothing when file has only blank cells`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("", "", "", "", ""),
                listOf("", "", "", "", ""),
                listOf("", "", "", "", "")
            )
        )
        // When
        dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(dataSource.readAll()).isEmpty()
        }

    @Test
    fun `update should return false when file has only blank cells`() {
        // Given
        writeInCsv(
            testFile, listOf(
                listOf("", "", "", "", ""),
                listOf("", "", "", "", ""),
                listOf("", "", "", "", "")
            )
        )
        // When
        val result=dataSource.update("1", UPDATED_PROJECT)
        // Then
        assertThat(result).isFalse()
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

    private fun testToRowMethodForProject(project: Project):List<String> {
       return listOf(
           project.id,
           project.name,
           project.description.toString(),
           "[${project.statesId.joinToString(",")}]",
           "[${project.tasksId.joinToString(",")}]")
    }

    private fun createDataSource(filePath: String): CsvDataSource<Project> {
        return object : CsvDataSource<Project>(
            filePath = filePath,
            headers = listOf("id", "name", "description", "statesId", "tasksId"),
            toRow =  ::testToRowMethodForProject ,
            fromRow = ::testFromRowMethodForProject
        ) {}
    }

    companion object{
        val UPDATED_PROJECT=Project(
            id = "1",
            name = "UpdatedProject",
            description = "UpdatedDescription",
            statesId = listOf("state4", "state5"),
            tasksId = listOf("task4")
        )
    }
}