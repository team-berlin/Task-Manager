package com.berlin.domain.permission

import com.berlin.domain.model.Permission
import com.berlin.domain.model.user.User

fun assignPermissions(role: User.UserRole): Permission = when (role) {
    User.UserRole.MATE -> Permission(
        createProject = false,
        updateProject = false,
        deleteProject = false,
        getAllProjects = false,
        getProjectById = false,

        createState = false,
        updateState = false,
        deleteState = false,
        getAllStatesByProjectId = false,
        getStateById = false,

        viewTaskById = true,
        viewTasksByProject = true,
        getTaskById = true,
        getTasksByProjectId = true,
        createTask = true,
        updateTask = true,
        deleteTask = true,

        assignTask = false,
        changeTaskState = false,

        viewAuditLogs = true,
        getAuditByProject = false,
        getAuditByTask = false,
        getAuditByUser = true,

        createMate = false,
        fetchAllUsers = false,
        getLoggedInUsers = true,
        getUserById = false
    )

    User.UserRole.ADMIN -> Permission(
        createProject = true,
        updateProject = true,
        deleteProject = true,
        getAllProjects = true,
        getProjectById = true,

        createState = true,
        updateState = true,
        deleteState = true,
        getAllStatesByProjectId = true,
        getStateById = true,

        viewTaskById = true,
        viewTasksByProject = true,
        getTaskById = true,
        getTasksByProjectId = true,
        createTask = true,
        updateTask = true,
        deleteTask = true,

        assignTask = true,
        changeTaskState = true,

        viewAuditLogs = true,
        getAuditByProject = true,
        getAuditByTask = true,
        getAuditByUser = true,

        createMate = true,
        fetchAllUsers = true,
        getLoggedInUsers = true,
        getUserById = true
    )
}
