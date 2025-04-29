package com.berlin.data.csv

import com.berlin.logic.repositories.StateRepository
import com.berlin.model.State
import java.io.File
import java.io.FileWriter

class StateRepositoryCSV(
    private val filePath: String = "states.csv"
) : StateRepository {

    private val delimiter = ","
    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.createNewFile()
            FileWriter(file).use { writer ->
                writer.append("id,name,projectId\n")
                // إضافة حالات افتراضية للاختبار
                writer.append("STATE-001,TODO,PROJ-001\n")
                writer.append("STATE-002,In Progress,PROJ-001\n")
                writer.append("STATE-003,Done,PROJ-001\n")
            }
        }
    }

    override fun createState(state: State): Boolean {
        return try {
            val existingStates = getAllStates()
            if (existingStates.any { it.id == state.id }) {
                return false
            }

            FileWriter(file, true).use { writer ->
                writer.append("${state.id}${delimiter}${state.name.replace(",", "\\,")}${delimiter}${state.projectId}\n")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getStatesByProjectId(projectId: String): List<State> {
        return getAllStates().filter { it.projectId == projectId }
    }

    override fun deleteState(stateId: String): Boolean {
        val states = getAllStates()
        val filteredStates = states.filter { it.id != stateId }

        if (states.size != filteredStates.size) {
            return writeAllStates(filteredStates)
        }
        return false
    }

    override fun updateState(state: State): Boolean {
        val states = getAllStates()
        val updatedStates = states.map { if (it.id == state.id) state else it }

        if (states.size == updatedStates.size) {
            return writeAllStates(updatedStates)
        }
        return false
    }

    override fun getStateByTaskId(taskId: String): State? {
        // هذه الوظيفة تحتاج إلى معرفة العلاقة بين المهام والحالات
        // في هذا التنفيذ البسيط، سنعيد null
        return null
    }

    private fun getAllStates(): List<State> {
        if (!file.exists()) return emptyList()

        return file.readLines()
            .drop(1) // تخطي العنوان
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    val parts = line.split(delimiter)
                    if (parts.size < 3) return@mapNotNull null

                    State(
                        id = parts[0],
                        name = parts[1].replace("\\,", ","),
                        projectId = parts[2]
                    )
                } catch (e: Exception) {
                    null
                }
            }
    }

    private fun writeAllStates(states: List<State>): Boolean {
        return try {
            FileWriter(file).use { writer ->
                writer.append("id,name,projectId\n")
                states.forEach { state ->
                    writer.append("${state.id}${delimiter}${state.name.replace(",", "\\,")}${delimiter}${state.projectId}\n")
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}