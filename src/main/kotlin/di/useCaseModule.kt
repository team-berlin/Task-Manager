package com.berlin.di

import com.berlin.domain.usecase.audit_system.AddAuditLogUseCase
import com.berlin.domain.usecase.audit_system.GetAuditLogsByProjectIdUseCase
import com.berlin.domain.usecase.audit_system.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.audit_system.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.domain.usecase.authService.GetAllUsersUseCase
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.domain.usecase.project.*
import com.berlin.domain.usecase.task.*
import com.berlin.domain.usecase.task_state.CreateTaskStateUseCase
import com.berlin.domain.usecase.task_state.DeleteTaskStateUseCase
import com.berlin.domain.usecase.task_state.GetAllTaskStatesByProjectIdUseCase
import com.berlin.domain.usecase.task_state.GetAllTaskStatesUseCase
import com.berlin.domain.usecase.task_state.GetTaskStateByIdUseCase
import com.berlin.domain.usecase.task_state.GetTaskStateByTaskIdUseCase
import com.berlin.domain.usecase.task_state.GetTasksByTaskStateIdUseCase
import com.berlin.domain.usecase.task_state.UpdateTaskStateUseCase
import com.berlin.domain.usecase.utils.validation.NonBlankNonNumericValidator
import com.berlin.domain.usecase.utils.validation.Validator
import domain.usecase.auth_service.LoginUserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::NonBlankNonNumericValidator) bind Validator::class

    singleOf(::CreateTaskUseCase)
    singleOf(::AssignTaskUseCase)
    singleOf(::DeleteTaskUseCase)
    singleOf(::GetTasksByProjectUseCase)
    singleOf(::UpdateTaskUseCase)
    singleOf(::ChangeTaskStateUseCase)
    singleOf(::GetTaskByIdUseCase)
    singleOf(::GetAllTasksUseCase)

    singleOf(::CreateProjectUseCase)
    singleOf(::GetAllProjectsUseCase)
    singleOf(::DeleteProjectUseCase)
    singleOf(::GetProjectByIdUseCase)
    singleOf(::UpdateProjectUseCase)

    singleOf(::AddAuditLogUseCase)
    singleOf(::GetAuditLogsByProjectIdUseCase)
    singleOf(::GetAuditLogsByTaskIdUseCase)
    singleOf(::GetAuditLogsByUserIdUseCase)

    singleOf(::GetUserByIDUseCase)
    singleOf(::GetUserLoggedInUseCase)
    singleOf(::GetAllUsersUseCase)
    singleOf(::LoginUserUseCase)
    singleOf(::CreateMateUseCase)

    singleOf(::CreateTaskStateUseCase)
    singleOf(::DeleteTaskStateUseCase)
    singleOf(::GetAllTaskStatesByProjectIdUseCase)
    singleOf(::GetTaskStateByIdUseCase)
    singleOf(::GetTaskStateByTaskIdUseCase)
    singleOf(::GetTasksByTaskStateIdUseCase)
    singleOf(::UpdateTaskStateUseCase)
    singleOf(::GetAllTaskStatesUseCase)
}