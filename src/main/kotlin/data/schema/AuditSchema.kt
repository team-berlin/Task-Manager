package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.AuditLog

class AuditSchema(
    override val fileName: String,
    override val header: List<String>
) :BaseSchema<AuditLog> {
    override fun toRow(entity: AuditLog): List<String> {
        TODO("Not yet implemented")
    }

    override fun fromRow(row:List<String>): AuditLog {
        TODO("Not yet implemented")
    }

    override fun getId(entity: AuditLog): String {
        TODO("Not yet implemented")
    }

}