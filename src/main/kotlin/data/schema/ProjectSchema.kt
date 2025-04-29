package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.Project

class ProjectSchema (
    override val fileName: String,
    override val header: List<String>
) : BaseSchema<Project> {
    override fun toRow(entity: Project): List<String> {
        TODO("Not yet implemented")
    }

    override fun fromRow(row:List<String>): Project {
        TODO("Not yet implemented")
    }

    override fun getId(entity: Project): String {
        TODO("Not yet implemented")
    }

}