package com.berlin.logic.repositories

import com.berlin.model.Task

interface TaskRepository {
    fun createTask(projectId:Int,task: Task):Boolean
    fun getTaskById(taskId:Int):Task?
    fun getTasksByProjectId(projectId:Int):List<Task>
    fun updateTask(task: Task):Boolean
    fun deleteTask(task: Task):Boolean
}