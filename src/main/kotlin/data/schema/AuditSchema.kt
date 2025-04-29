package com.berlin.data.schema

import com.berlin.data.BaseSchema
import com.berlin.model.AuditLog

class AuditSchema(
    override val fileName: String,
    override val header: List<String>
) :BaseSchema<AuditLog> {
    override fun toRow(entity: AuditLog): List<String> {
        return emptyList()
    }

    override fun fromRow(row:List<String>): AuditLog? {
        return null
    }

    override fun getId(entity: AuditLog): String {
        return ""
    }

}