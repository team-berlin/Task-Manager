package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.Project

class ProjectSchema (
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<Project> {
    override fun toRow(entity: Project): List<String> {
        return emptyList()
    }

    override fun fromRow(row:List<String>): Project? {
        return null
    }

    override fun getId(entity: Project): String {
        return ""
    }

}