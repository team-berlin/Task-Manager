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
import data.UserCache
import domain.usecase.auth_service.LoginUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single <Validator> { NonBlankNonNumericValidator() }
    single { CreateTaskUseCase(get(), get(), get(),get<Validator>()) }
    single { AssignTaskUseCase(get(), get(), get<UserCache>()) }
    single { DeleteTaskUseCase(get(), get(), get<UserCache>(),get<Validator>()) }
    single { GetTasksByProjectUseCase(get(),get<Validator>()) }
    single { UpdateTaskUseCase(get(), get(), get<UserCache>()) }
    single { ChangeTaskStateUseCase(get(), get(), get<UserCache>(),get<Validator>()) }
    single { GetTaskByIdUseCase(get(),get<Validator>()) }
    single { GetAllTasksUseCase(get()) }

    single { CreateProjectUseCase(get(), get(), get(), get<UserCache>(),get<Validator>()) }
    single { GetAllProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get(), get(), get<UserCache>(),get<Validator>()) }
    single { GetProjectByIdUseCase(get(),get<Validator>()) }
    single { UpdateProjectUseCase(get(), get(), get<UserCache>(),get<Validator>() ) }

    single { AddAuditLogUseCase(get(), get()) }
    single { GetAuditLogsByProjectIdUseCase(get(),get<Validator>()) }
    single { GetAuditLogsByTaskIdUseCase(get(),get<Validator>())}
    single { GetAuditLogsByUserIdUseCase(get(),get<Validator>())}

    single { GetUserByIDUseCase(get()) }
    single { GetUserLoggedInUseCase(get()) }
    single { GetAllUsersUseCase(get()) }
    single { LoginUserUseCase(get(),get(), get()) }
    single { CreateMateUseCase(get(), get(), get()) }

    single { CreateTaskStateUseCase(get(), get(),get<Validator>()) }
    single { DeleteTaskStateUseCase(get(),get<Validator>()) }
    single { GetAllTaskStatesByProjectIdUseCase(get(), get(),get<Validator>()) }
    single { GetTaskStateByIdUseCase(get(),get<Validator>()) }
    single { GetTaskStateByTaskIdUseCase(get(), get(),get<Validator>()) }
    single { GetTasksByTaskStateIdUseCase(get(),get<Validator>()) }
    single { UpdateTaskStateUseCase(get(),get<Validator>()) }
    single { GetAllTaskStatesUseCase(get()) }
}