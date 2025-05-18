package com.berlin.data.csv_data_source.schema

import com.berlin.data.ProjectIndex
import com.berlin.data.dto.ProjectDto

class ProjectSchema(
    override val fileName: String, override val header: List<String>
) : BaseSchema<ProjectDto> {

    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: ProjectDto): List<String> {
        return if (checkProjectDtoIsNotValid(entity).not()) {
            mapProjectDtoToList(entity)
        } else {
            emptyList()
        }
    }

    override fun fromRow(row: List<String>): ProjectDto? {
        return if (checkRowIsNotValidProjectDto(row).not()) {
            mapListToProjectDto(row)
        } else {
            null
        }
    }

    override fun getId(entity: ProjectDto): String? {
        return entity.id.ifEmpty { null }
    }

    private fun mapProjectDtoToList(project: ProjectDto): List<String> {
        return listOf(
            project.id,
            project.title,
            project.description ?: "",
            project.statesId?.joinToString(",", "[", "]") ?: "[]",
            project.tasksId?.joinToString(",", "[", "]") ?: "[]"
        )
    }

    private fun mapListToProjectDto(row: List<String>): ProjectDto {
        return ProjectDto(
            id = row[ProjectIndex.ID],
            title = row[ProjectIndex.NAME],
            description = row[ProjectIndex.DESCRIPTION].ifEmpty { null },
            statesId = row[ProjectIndex.STATES_ID].let {
                if ((it == "[]").not()) {
                    mapRowToList(it)
                } else {
                    null
                }
            },
            tasksId = row[ProjectIndex.TASKS_ID].let {
                if ((it == "[]").not()) {
                    mapRowToList(it)
                } else {
                    null
                }
            })
    }

    private fun checkRowIsNotValidProjectDto(row: List<String>): Boolean {
        return (row.isEmpty() || row[ProjectIndex.ID].isEmpty() || row[ProjectIndex.NAME].isEmpty())
    }

    private fun checkProjectDtoIsNotValid(project: ProjectDto): Boolean {
        return project.id.isEmpty() || project.title.isEmpty()
    }

    private fun mapRowToList(row: String): List<String> {
        return row.removeSurrounding("[", "]").split(",")
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 5
    }
}