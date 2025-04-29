package com.berlin.ui

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.repositories.StateRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.logic.usecases.task.AssignTaskUseCase
import com.berlin.logic.usecases.task.CreateTaskUseCase
import com.berlin.logic.usecases.task.DeleteTaskUseCase
import com.berlin.logic.usecases.task.UpdateTaskUseCase
import com.berlin.model.Task
import com.berlin.model.User
import com.berlin.model.UserRole

class TaskUI(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val stateRepository: StateRepository,
    private val authRepository: AuthenticationRepository,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val assignTaskUseCase: AssignTaskUseCase
) {
    fun displayTaskMenu(currentUser: User) {
        while (true) {
            println("\n===== Task Management =====")
            println("1. Create a new task")
            println("2. View task details")
            println("3. Update a task")
            println("4. Delete a task")
            println("5. Assign a task")
            println("6. View tasks by project")
            println("7. Back to main menu")
            print("Enter your choice: ")

            when (readLine()?.trim()) {
                "1" -> createTask(currentUser)
                "2" -> viewTaskDetails()
                "3" -> updateTask(currentUser)
                "4" -> deleteTask(currentUser)
                "5" -> assignTask(currentUser)
                "6" -> viewTasksByProject()
                "7" -> return
                else -> println("Invalid option. Please try again.")
            }
        }
    }

    private fun createTask(currentUser: User) {
        println("\n===== Create New Task =====")

        // Get all projects
        val projects = projectRepository.getAllProjects()
        if (projects.isEmpty()) {
            println("No projects available. Please create a project first.")
            return
        }

        // Display projects
        println("Available Projects:")
        projects.forEachIndexed { index, project ->
            println("${index + 1}. ${project.name}")
        }

        // Select project
        print("Select project (enter number): ")
        val projectIndex = readLine()?.toIntOrNull()?.minus(1) ?: -1
        if (projectIndex < 0 || projectIndex >= projects.size) {
            println("Invalid project selection.")
            return
        }

        val selectedProject = projects[projectIndex]

        // Get states for the selected project
        val states = stateRepository.getStatesByProjectId(selectedProject.id)
        if (states.isEmpty()) {
            println("No states available for this project. Please create states first.")
            return
        }

        // Display states
        println("Available States:")
        states.forEachIndexed { index, state ->
            println("${index + 1}. ${state.name}")
        }

        // Select state
        print("Select state (enter number): ")
        val stateIndex = readLine()?.toIntOrNull()?.minus(1) ?: -1
        if (stateIndex < 0 || stateIndex >= states.size) {
            println("Invalid state selection.")
            return
        }

        val selectedState = states[stateIndex]

        // Get users for assignment
        val users = authRepository.getAllUsers()
        if (users.isEmpty()) {
            println("No users available for assignment.")
            return
        }

        // Display users
        println("Available Users for Assignment:")
        users.forEachIndexed { index, user ->
            println("${index + 1}. ${user.userName}")
        }

        // Select user for assignment
        print("Select user to assign (enter number): ")
        val userIndex = readLine()?.toIntOrNull()?.minus(1) ?: -1
        if (userIndex < 0 || userIndex >= users.size) {
            println("Invalid user selection.")
            return
        }

        val selectedUser = users[userIndex]

        // Get task details
        print("Enter task title: ")
        val title = readLine()?.trim() ?: ""
        if (title.isEmpty()) {
            println("Title cannot be empty.")
            return
        }

        print("Enter task description (optional): ")
        val description = readLine()?.trim()

        // Create the task
        val success = createTaskUseCase.execute(
            projectId = selectedProject.id,
            title = title,
            description = description,
            stateId = selectedState.id,
            assignedTo = selectedUser,
            createdBy = currentUser
        )

        if (success) {
            println("Task created successfully!")
        } else {
            println("Failed to create task.")
        }
    }

    private fun viewTaskDetails() {
        print("Enter task ID: ")
        val taskId = readLine()?.trim() ?: ""

        val task = taskRepository.getTaskById(taskId)
        if (task == null) {
            println("Task not found.")
            return
        }

        displayTaskDetails(task)
    }

    private fun updateTask(currentUser: User) {
        print("Enter task ID to update: ")
        val taskId = readLine()?.trim() ?: ""

        val task = taskRepository.getTaskById(taskId)
        if (task == null) {
            println("Task not found.")
            return
        }

        // Check permissions
        if (currentUser.role != UserRole.ADMIN && task.createBy.id != currentUser.id && task.assignedTo.id != currentUser.id) {
            println("You don't have permission to update this task.")
            return
        }

        println("\nCurrent Task Details:")
        displayTaskDetails(task)

        // Get states for the task's project
        val states = stateRepository.getStatesByProjectId(task.projectId)

        // Display states
        println("\nAvailable States:")
        states.forEachIndexed { index, state ->
            println("${index + 1}. ${state.name}" + if (state.id == task.stateId) " (current)" else "")
        }

        // Select state
        print("Select new state (enter number or leave empty to keep current): ")
        val stateInput = readLine()?.trim() ?: ""
        val stateIndex = stateInput.toIntOrNull()?.minus(1)
        val newStateId = if (stateIndex != null && stateIndex >= 0 && stateIndex < states.size) {
            states[stateIndex].id
        } else {
            task.stateId
        }

        // Get users for assignment
        val users = authRepository.getAllUsers()

        // Display users
        println("\nAvailable Users for Assignment:")
        users.forEachIndexed { index, user ->
            println("${index + 1}. ${user.userName}" + if (user.id == task.assignedTo.id) " (current)" else "")
        }

        // Select user for assignment
        print("Select new user to assign (enter number or leave empty to keep current): ")
        val userInput = readLine()?.trim() ?: ""
        val userIndex = userInput.toIntOrNull()?.minus(1)
        val newAssignedTo = if (userIndex != null && userIndex >= 0 && userIndex < users.size) {
            users[userIndex]
        } else {
            task.assignedTo
        }

        // Update title and description
        print("Enter new title (or leave empty to keep current '${task.title}'): ")
        val titleInput = readLine()?.trim() ?: ""
        val newTitle = if (titleInput.isNotEmpty()) titleInput else task.title

        print("Enter new description (or leave empty to keep current): ")
        val descInput = readLine()?.trim()
        val newDescription = if (descInput != null) descInput else task.description

        // Update the task
        val success = updateTaskUseCase.execute(
            taskId = taskId,
            title = newTitle,
            description = newDescription,
            stateId = newStateId,
            assignedTo = newAssignedTo,
            updatedBy = currentUser
        )

        if (success) {
            println("Task updated successfully!")
        } else {
            println("Failed to update task.")
        }
    }

    private fun deleteTask(currentUser: User) {
        print("Enter task ID to delete: ")
        val taskId = readLine()?.trim() ?: ""

        val task = taskRepository.getTaskById(taskId)
        if (task == null) {
            println("Task not found.")
            return
        }

        // Check permissions
        if (currentUser.role != UserRole.ADMIN && task.createBy.id != currentUser.id) {
            println("You don't have permission to delete this task.")
            return
        }

        println("\nTask to delete:")
        displayTaskDetails(task)

        print("Are you sure you want to delete this task? (y/n): ")
        val confirm = readLine()?.trim()?.lowercase() ?: ""

        if (confirm == "y" || confirm == "yes") {
            val success = deleteTaskUseCase.execute(taskId, currentUser)

            if (success) {
                println("Task deleted successfully!")
            } else {
                println("Failed to delete task.")
            }
        } else {
            println("Task deletion cancelled.")
        }
    }

    private fun assignTask(currentUser: User) {
        print("Enter task ID to assign: ")
        val taskId = readLine()?.trim() ?: ""

        val task = taskRepository.getTaskById(taskId)
        if (task == null) {
            println("Task not found.")
            return
        }

        // Check permissions
        if (currentUser.role != UserRole.ADMIN && task.createBy.id != currentUser.id) {
            println("You don't have permission to assign this task.")
            return
        }

        println("\nCurrent Task Assignment:")
        println("Task: ${task.title}")
        println("Currently assigned to: ${task.assignedTo.userName}")

        // Get users for assignment
        val users = authRepository.getAllUsers()

        // Display users
        println("\nAvailable Users for Assignment:")
        users.forEachIndexed { index, user ->
            println("${index + 1}. ${user.userName}" + if (user.id == task.assignedTo.id) " (current)" else "")
        }

        // Select user for assignment
        print("Select user to assign (enter number): ")
        val userIndex = readLine()?.toIntOrNull()?.minus(1) ?: -1
        if (userIndex < 0 || userIndex >= users.size) {
            println("Invalid user selection.")
            return
        }

        val selectedUser = users[userIndex]

        if (selectedUser.id == task.assignedTo.id) {
            println("Task is already assigned to this user.")
            return
        }

        val success = assignTaskUseCase.execute(taskId, selectedUser, currentUser)

        if (success) {
            println("Task assigned successfully!")
        } else {
            println("Failed to assign task.")
        }
    }

    private fun viewTasksByProject() {
        // Get all projects
        val projects = projectRepository.getAllProjects()
        if (projects.isEmpty()) {
            println("No projects available.")
            return
        }

        // Display projects
        println("Available Projects:")
        projects.forEachIndexed { index, project ->
            println("${index + 1}. ${project.name}")
        }

        // Select project
        print("Select project (enter number): ")
        val projectIndex = readLine()?.toIntOrNull()?.minus(1) ?: -1
        if (projectIndex < 0 || projectIndex >= projects.size) {
            println("Invalid project selection.")
            return
        }

        val selectedProject = projects[projectIndex]

        // Get states for the selected project
        val states = stateRepository.getStatesByProjectId(selectedProject.id)
        if (states.isEmpty()) {
            println("No states available for this project.")
            return
        }

        // Get tasks for the selected project
        val tasks = taskRepository.getTasksByProjectId(selectedProject.id)

        // Display tasks by state (swimlanes)
        println("\n=== Project: ${selectedProject.name} ===")
        println("==================================================")

        states.forEach { state ->
            println("\nState: [${state.id}] ${state.name}")
            println("--------------------------------------------------")

            val tasksInState = tasks.filter { it.stateId == state.id }

            if (tasksInState.isEmpty()) {
                println("No tasks in this state.")
            } else {
                tasksInState.forEach { task ->
                    println("- Task ID: ${task.id}, Title: ${task.title}, Assigned to: ${task.assignedTo.userName}")
                }
            }
        }

        println("==================================================")
    }

    private fun displayTaskDetails(task: Task) {
        println("\n===== Task Details =====")
        println("ID: ${task.id}")
        println("Title: ${task.title}")
        println("Description: ${task.description ?: "N/A"}")
        println("Project ID: ${task.projectId}")
        println("State ID: ${task.stateId}")
        println("Assigned To: ${task.assignedTo.userName}")
        println("Created By: ${task.createBy.userName}")

        if (task.auditLogs.isNotEmpty()) {
            println("\nAudit Logs:")
            task.auditLogs.sortedByDescending { it.timestamp }.forEach { log ->
                println("- ${log.timestamp}: ${log.createdBy.userName} - ${log.changesDescription}")
            }
        }
    }
}