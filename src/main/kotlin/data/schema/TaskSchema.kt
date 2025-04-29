package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.Task

class TaskSchema(
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<Task> {
    override fun toRow(entity: Task): List<String> {
        return emptyList()
    }

    override fun fromRow(row:List<String>): Task? {
        return null
    }

    override fun getId(entity: Task): String {
        return ""
    }

}