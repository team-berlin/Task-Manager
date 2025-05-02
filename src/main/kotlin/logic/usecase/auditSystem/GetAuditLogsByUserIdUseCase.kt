package com.berlin.logic.usecase.auditSystem

import com.berlin.domain.model.AuditLog
import com.berlin.logic.repositories.AuditRepository

class GetAuditLogsByUserIdUseCase(
    private val auditRepository: AuditRepository
) {

    fun getAuditLogsByUserId(userId:String):List<AuditLog>{

        if (!validateUserId(userId))
            throw IllegalArgumentException("User ID must not be empty, blank, or purely numeric")

        return auditRepository.getAuditLogsByUserId(userId)

    }

    private fun validateUserId(projectId: String): Boolean =
        projectId.isNotBlank() && !(projectId.all { it.isDigit() })
}
