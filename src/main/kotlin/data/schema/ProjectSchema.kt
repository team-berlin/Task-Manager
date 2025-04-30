package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.data.ProjectIndex
import com.berlin.model.Project

class ProjectSchema(
    override val fileName: String, override val header: List<String>
) : BaseSchema<Project> {
    init {
        require(fileName.isNotEmpty() && header.size == NUMBER_OF_ATTRIBUTES)
    }

    override fun toRow(entity: Project): List<String> {
        return if (entity.id.isEmpty() || entity.name.isEmpty()) emptyList()
        else listOf(
            entity.id,
            entity.name,
            entity.description ?: "",
            "[${entity.statesId.joinToString(",")}]",
            "[${entity.tasksId.joinToString(",")}]"
        )
    }

    override fun fromRow(row: List<String>): Project? {
        return if (row[ProjectIndex.ID].isEmpty() || row[ProjectIndex.NAME].isEmpty()) null
        else Project(
            id = row[ProjectIndex.ID],
            name = row[ProjectIndex.NAME],
            description = row[ProjectIndex.DESCRIPTION].ifEmpty { null },
            statesId = row[ProjectIndex.STATES_ID]
                .removeSurrounding("[", "]")
                .takeIf { it.isNotEmpty() }?.split(",")?: emptyList(),
            tasksId = row[ProjectIndex.TASKS_ID]
                .removeSurrounding("[", "]")
                .takeIf { it.isNotEmpty() }?.split(",")?: emptyList()
        )
    }

    override fun getId(entity: Project): String {
        return entity.id.ifEmpty { "" }
    }

    private companion object {
        const val NUMBER_OF_ATTRIBUTES = 5
    }
}