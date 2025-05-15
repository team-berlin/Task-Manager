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
import data.UserCache
import domain.usecase.auth_service.LoginUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { CreateTaskUseCase(get(), get(), get()) }
    single { AssignTaskUseCase(get(), get(), get<UserCache>()) }
    single { DeleteTaskUseCase(get(), get(), get<UserCache>()) }
    single { GetTasksByProjectUseCase(get()) }
    single { UpdateTaskUseCase(get(), get(), get<UserCache>()) }
    single { ChangeTaskStateUseCase(get(), get(), get<UserCache>()) }
    single { GetTaskByIdUseCase(get()) }
    single { GetAllTasksUseCase(get()) }

    single { CreateProjectUseCase(get(), get(), get(), get<UserCache>()) }
    single { GetAllProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get(), get(), get<UserCache>()) }
    single { GetProjectByIdUseCase(get()) }
    single { UpdateProjectUseCase(get(), get(), get<UserCache>() ) }

    single { AddAuditLogUseCase(get(), get()) }
    single { GetAuditLogsByProjectIdUseCase(get()) }
    single { GetAuditLogsByTaskIdUseCase(get()) }
    single { GetAuditLogsByUserIdUseCase(get()) }

    single { GetUserByIDUseCase(get()) }
    single { GetUserLoggedInUseCase(get()) }
    single { GetAllUsersUseCase(get()) }
    single { LoginUserUseCase(get(),get(), get()) }
    single { CreateMateUseCase(get(), get(), get()) }

    single { CreateTaskStateUseCase(get(), get()) }
    single { DeleteTaskStateUseCase(get()) }
    single { GetAllTaskStatesByProjectIdUseCase(get(), get()) }
    single { GetTaskStateByIdUseCase(get()) }
    single { GetTaskStateByTaskIdUseCase(get()) }
    single { GetTasksByTaskStateIdUseCase(get()) }
    single { UpdateTaskStateUseCase(get()) }
    single { GetAllTaskStatesUseCase(get()) }
}