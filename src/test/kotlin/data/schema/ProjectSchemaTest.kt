package com.berlin.data.schema

import com.berlin.data.csv_data_source.schema.ProjectSchema
import com.berlin.data.dto.ProjectDto
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ProjectSchemaTest {

    private lateinit var projectSchema: ProjectSchema

    @BeforeEach
    fun setup() {
        projectSchema = fakeProjectSchema()
    }

    //region create object

    @Test
    fun `should throw IllegalArgumentException when try to create object with blank file name`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            projectSchema = ProjectSchema("", listOf("a", "b", "c", "d", "e"))
        }
    }

    @Test
    fun `should throw IllegalArgumentException when try to create object with invalid size header`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            projectSchema = ProjectSchema("test.csv", listOf("a", "b"))
        }
    }

    //endregion

    //region toRow

    @Test
    fun `toRow should return list of valid project attributes when valid project passed`() {
        //when
        val result = projectSchema.toRow(validProject)
        //then
        assertThat(result).isEqualTo(validRow)
    }

    @Test
    fun `toRow should return list of valid project attributes when project with empty statesId passed`() {
        //when
        val result = projectSchema.toRow(validProjectEmptyStatesId)
        //then
        assertThat(result).isEqualTo(validRowEmptyStatesId)
    }

    @Test
    fun `toRow should return list of valid project attributes when project with empty description passed`() {
        //when
        val result = projectSchema.toRow(validProjectEmptyDescription)
        //then
        assertThat(result).isEqualTo(validRowEmptyDescription)
    }

    @Test
    fun `toRow should return list of valid project attributes when project with empty tasksId passed`() {
        //when
        val result = projectSchema.toRow(validProjectEmptyTasksId)
        //then
        assertThat(result).isEqualTo(validRowEmptyTasksId)
    }

    @Test
    fun `toRow should return empty list when invalid project passed miss id attribute`() {
        //when
        val result = projectSchema.toRow(invalidProjectEmptyId)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid project passed miss name attribute`() {
        //when
        val result = projectSchema.toRow(invalidProjectEmptyName)
        //then
        assertThat(result).isEmpty()
    }

    //endregion

    //region fromRow

    @Test
    fun `fromRow should return project when valid row full passed`() {
        //when
        val result = projectSchema.fromRow(validRow)
        //then
        assertThat(result).isEqualTo(validProject)
    }

    @Test
    fun `fromRow should return project when valid row empty description passed`() {
        //when
        val result = projectSchema.fromRow(validRowEmptyDescription)
        //then
        assertThat(result).isEqualTo(validProjectEmptyDescription)
    }

    @Test
    fun `fromRow should return project when valid row empty statesId passed`() {
        //when
        val result = projectSchema.fromRow(validRowEmptyStatesId)
        //then
        assertThat(result).isEqualTo(validProjectEmptyStatesId)
    }

    @Test
    fun `fromRow should return project when valid row empty tasksId passed`() {
        //when
        val result = projectSchema.fromRow(validRowEmptyTasksId)
        //then
        assertThat(result).isEqualTo(validProjectEmptyTasksId)
    }

    @Test
    fun `fromRow should return null when invalid row passed miss id column`() {
        //when
        val result = projectSchema.fromRow(invalidRowEmptyId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when empty row passed miss id column`() {
        //when
        val result = projectSchema.fromRow(emptyList())
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid project passed miss name column`() {
        //when
        val result = projectSchema.fromRow(invalidRowEmptyName)
        //then
        assertThat(result).isNull()
    }

    //endregion

    //region getId

    @Test
    fun `getId should return id of project passed`() {
        //when
        val result = projectSchema.getId(validProject)
        //then
        assertThat(result).isEqualTo(validProject.id)
    }

    @Test
    fun `getId should return null when project passed have empty id`() {
        //when
        val result = projectSchema.getId(invalidProjectEmptyId)
        //then
        assertThat(result).isNull()
    }

    //endregion

    private fun fakeProjectSchema() = ProjectSchema("test.csv", listOf("a", "b", "c", "d", "e"))

    private companion object {
        //region Some Projects

        val validProject = ProjectDto(
            id = "proj123",
            title = "SampleProject",
            description = "A sample project",
            statesId = listOf("state1", "state2"),
            tasksId = listOf("task1", "task2")
        )
        val validProjectEmptyDescription = ProjectDto(
            id = "proj123",
            title = "SampleProject",
            description = null,
            statesId = listOf("state1", "state2"),
            tasksId = listOf("task1", "task2")
        )
        val validProjectEmptyStatesId = ProjectDto(
            id = "proj123",
            title = "SampleProject",
            description = "A sample project",
            statesId = null,
            tasksId = listOf("task1", "task2")
        )
        val validProjectEmptyTasksId = ProjectDto(
            id = "proj123",
            title = "SampleProject",
            description = "A sample project",
            statesId = listOf("state1", "state2"),
            tasksId = null
        )
        val invalidProjectEmptyId = ProjectDto(
            id = "",
            title = "SampleProject",
            description = "A sample project",
            statesId = listOf("state1", "state2"),
            tasksId = listOf("task1", "task2")
        )
        val invalidProjectEmptyName = ProjectDto(
            id = "proj123",
            title = "",
            description = "A sample project",
            statesId = listOf("state1", "state2"),
            tasksId = listOf("task1", "task2")
        )

        //endregion

        //region Some Rows

        val validRow = listOf(
            "proj123", "SampleProject", "A sample project", "[state1,state2]", "[task1,task2]"
        )
        val validRowEmptyDescription = listOf(
            "proj123", "SampleProject", "", "[state1,state2]", "[task1,task2]"
        )
        val validRowEmptyStatesId = listOf(
            "proj123", "SampleProject", "A sample project", "[]", "[task1,task2]"
        )
        val validRowEmptyTasksId = listOf(
            "proj123", "SampleProject", "A sample project", "[state1,state2]", "[]"
        )
        val invalidRowEmptyId = listOf(
            "", "SampleProject", "A sample project", "[state1,state2]", "[task1,task2]"
        )
        val invalidRowEmptyName = listOf(
            "proj123", "", "A sample project", "[state1,state2]", "[task1,task2]"
        )

        //endregion
    }
}