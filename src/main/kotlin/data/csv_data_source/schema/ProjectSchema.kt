package com.berlin.data.csv_data_source.schema

import com.berlin.data.ProjectIndex
import com.berlin.data.dto.ProjectDto

class ProjectSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<ProjectDto> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: ProjectDto): List<String> {
        return if (checkProjectIsNotValid(entity)) emptyList()
        else projectToStringsList(entity)
    }

    override fun fromRow(row: List<String>): ProjectDto? {
        return if (checkRowIsNotValidProject(row)) null
        else stringsListToProject(row)
    }

    override fun getId(entity: ProjectDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun projectToStringsList(project: ProjectDto): List<String> {
        return listOf(
            project.id,
            project.title,
            project.description ?: "",
            project.statesId?.joinToString(",", "[", "]") ?: "[]",
            project.tasksId?.joinToString(",", "[", "]") ?: "[]"
        )
    }

    private fun stringsListToProject(row: List<String>): ProjectDto {
        return ProjectDto(
            id = row[ProjectIndex.ID],
            title = row[ProjectIndex.NAME],
            description = row[ProjectIndex.DESCRIPTION].ifEmpty { null },
            statesId = row[ProjectIndex.STATES_ID].let { if (it == "[]") null else stringListToList(it) },
            tasksId = row[ProjectIndex.TASKS_ID].let { if (it == "[]") null else stringListToList(it) }
        )
    }

    private fun checkRowIsNotValidProject(row: List<String>): Boolean {
        return (row.isEmpty()||
                row[ProjectIndex.ID].isEmpty() ||
                row[ProjectIndex.NAME].isEmpty())
    }

    private fun checkProjectIsNotValid(project: ProjectDto): Boolean {
        return project.id.isEmpty() ||
                project.title.isEmpty()
    }

    private fun stringListToList(listString: String): List<String> {
        return listString
            .removeSurrounding("[", "]")
            .split(",")
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 5
    }
}