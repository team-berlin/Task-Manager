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
import com.berlin.domain.usecase.state.CreateStateUseCase
import com.berlin.domain.usecase.state.DeleteStateUseCase
import com.berlin.domain.usecase.state.GetAllStatesByProjectIdUseCase
import com.berlin.domain.usecase.state.GetAllStatesUseCase
import com.berlin.domain.usecase.state.GetStateByIdUseCase
import com.berlin.domain.usecase.state.GetStateByTaskIdUseCase
import com.berlin.domain.usecase.state.GetTasksByStateIdUseCase
import com.berlin.domain.usecase.state.UpdateStateUseCase
import com.berlin.domain.usecase.task.*
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

    single { CreateStateUseCase(get(),get())}
    single { DeleteStateUseCase(get()) }
    single { GetAllStatesByProjectIdUseCase(get(),get()) }
    single { GetStateByIdUseCase(get()) }
    single { GetStateByTaskIdUseCase(get(),get()) }
    single { GetTasksByStateIdUseCase(get()) }
    single { UpdateStateUseCase(get()) }
    single { GetAllStatesUseCase(get()) }
}