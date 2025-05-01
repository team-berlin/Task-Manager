package com.berlin.logic.permission

import com.berlin.domain.model.UserRole
import domain.model.Permission

fun assignPermissions(role: UserRole): Permission {
    return when(role){
        UserRole.MATE ->{
            Permission(
                createTask = true,
                editTask = true,
                deleteTask= true,
                viewAuditLogs= true
            )
        }
        UserRole.ADMIN ->{
            Permission(
             createProject = true,
             editProject= true,
             deleteProject = true,
             createTask = true,
             editTask = true,
             deleteTask= true,
             assignTask = true,
             viewAuditLogs= true
            )
        }
    }

}