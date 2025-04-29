package com.berlin.data.csv

import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.AuditAction
import com.berlin.model.AuditLog
import com.berlin.model.EntityType
import java.io.File
import java.io.FileWriter
import org.koin.java.KoinJavaComponent.inject

class AuditRepositoryCSV(
    private val filePath: String = "audit_logs.csv"
) : AuditRepository {

    private val delimiter = ","
    private val file = File(filePath)
    private val authRepository: AuthenticationRepository by inject(AuthenticationRepository::class.java)

    init {
        if (!file.exists()) {
            file.createNewFile()
            FileWriter(file).use { writer ->
                writer.append("id,timestamp,createdById,auditAction,changesDescription,entityType,entityId\n")
            }
        }
    }

    override fun addAuditLog(auditLog: AuditLog): Boolean {
        return try {
            FileWriter(file, true).use { writer ->
                writer.append("${auditLog.id}${delimiter}")
                writer.append("${auditLog.timestamp}${delimiter}")
                writer.append("${auditLog.createdBy.id}${delimiter}")
                writer.append("${auditLog.auditAction}${delimiter}")
                writer.append("${auditLog.changesDescription?.replace(",", "\\,")}${delimiter}")
                writer.append("${auditLog.entityType}${delimiter}")
                writer.append("${auditLog.entityId}\n")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getAuditLogsByProjectId(projectId: String): List<AuditLog> {
        return getAllAuditLogs().filter {
            it.entityType == EntityType.PROJECT && it.entityId == projectId
        }
    }

    override fun getAuditLogsByTaskId(taskId: String): List<AuditLog> {
        return getAllAuditLogs().filter {
            it.entityType == EntityType.TASK && it.entityId == taskId
        }
    }

    override fun getAuditLogsByUserId(userId: String): List<AuditLog> {
        return getAllAuditLogs().filter {
            it.createdBy.id == userId
        }
    }

    private fun getAllAuditLogs(): List<AuditLog> {
        if (!file.exists()) return emptyList()

        return file.readLines()
            .drop(1) // تخطي العنوان
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    val parts = line.split(delimiter)
                    if (parts.size < 7) return@mapNotNull null

                    val id = parts[0]
                    val timestamp = parts[1].toLongOrNull() ?: 0L
                    val createdById = parts[2]
                    val auditAction = AuditAction.valueOf(parts[3])
                    val changesDescription = parts[4].replace("\\,", ",").takeIf { it.isNotBlank() }
                    val entityType = EntityType.valueOf(parts[5])
                    val entityId = parts[6]

                    val createdBy = authRepository.getUserById(createdById) ?: return@mapNotNull null

                    AuditLog(
                        id = id,
                        timestamp = timestamp,
                        createdBy = createdBy,
                        auditAction = auditAction,
                        changesDescription = changesDescription,
                        entityType = entityType,
                        entityId = entityId
                    )
                } catch (e: Exception) {
                    null
                }
            }
    }
}