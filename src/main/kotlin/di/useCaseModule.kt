package com.berlin.di

import com.berlin.domain.usecase.auditSystem.AddAuditLogUseCase
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByProjectIdUseCase
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByTaskIdUseCase
import com.berlin.domain.usecase.auditSystem.GetAuditLogsByUserIdUseCase
import com.berlin.domain.usecase.authService.CreateMateUseCase
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GetUserLoggedInUseCase
import com.berlin.domain.usecase.project.*
import com.berlin.domain.usecase.task.*
import data.UserCache
import domain.usecase.authService.AuthenticateUserUseCase
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
    single { FetchAllUsersUseCase(get()) }
    single { AuthenticateUserUseCase(get(),get(), get()) }
    single { CreateMateUseCase(get(), get(), get()) }
}
