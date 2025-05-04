package com.berlin.di

import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GettingUsersLoggedInUseCase
import com.berlin.domain.usecase.task.*
import domain.usecase.authService.AuthenticateUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { CreateTaskUseCase(get(), get()) }
    single { AssignTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { ChangeTaskStateUseCase(get()) }
    single { GetTaskByIdUseCase(get()) }
    single { GetAllTasksUseCase(get()) }
    single { GetUserByIDUseCase(get()) }
    single { GettingUsersLoggedInUseCase(get()) }
    single { FetchAllUsersUseCase(get()) }
    single { AuthenticateUserUseCase(get(),get()) }
    single { CreationOfMateUseCase(get(),get()) }
}
