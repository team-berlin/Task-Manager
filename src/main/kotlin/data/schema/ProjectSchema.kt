package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.ProjectIndex
import com.berlin.domain.model.Project

class ProjectSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<Project> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override suspend fun toRow(entity: Project): List<String> {
        return if (checkProjectIsNotValid(entity)) emptyList()
        else projectToStringsList(entity)
    }

    override suspend fun fromRow(row: List<String>): Project? {
        return if (checkRowIsNotValidProject(row)) null
        else stringsListToProject(row)
    }

    override suspend fun getId(entity: Project): String? {
        return entity.id.ifEmpty { null }
    }

    private fun projectToStringsList(project: Project): List<String> {
        return listOf(
            project.id,
            project.name,
            project.description ?: "",
            project.statesId?.joinToString(",", "[", "]") ?: "[]",
            project.tasksId?.joinToString(",", "[", "]") ?: "[]"
        )
    }

    private fun stringsListToProject(row: List<String>): Project {
        return Project(
            id = row[ProjectIndex.ID],
            name = row[ProjectIndex.NAME],
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

    private fun checkProjectIsNotValid(project: Project): Boolean {
        return project.id.isEmpty() ||
                project.name.isEmpty()
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