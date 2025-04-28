package com.berlin.logic.repositories

import com.berlin.model.Task
import com.berlin.model.User

interface TaskRepository {
    fun createTask(task:Task):Boolean
    fun getTaskById(taskId:String):Task?
    fun getTasksByProjectId(projectId:String):List<Task>
    fun updateTask(task: Task):Boolean
    fun deleteTaskById(taskId: String):Boolean
    fun getAssignedUserByTaskId(taskId: String):User?
}