package com.berlin.di

import com.berlin.data.memory.InMemoryAuthRepositoryImpl
import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.usecase.authService.CreationOfMateUseCase
import com.berlin.domain.usecase.authService.FetchAllUsersUseCase
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GettingUsersLoggedInUseCase
import com.berlin.domain.usecase.task.AssignTaskUseCase
import com.berlin.domain.usecase.task.ChangeTaskStateUseCase
import com.berlin.domain.usecase.task.CreateTaskUseCase
import com.berlin.domain.usecase.task.DeleteTaskUseCase
import com.berlin.domain.usecase.task.GetTaskByIdUseCase
import com.berlin.domain.usecase.task.GetTasksByProjectUseCase
import com.berlin.domain.usecase.task.UpdateTaskUseCase
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
    single { AuthenticateUserUseCase(get()) }
    single { CreationOfMateUseCase(get(),get()) }
    single { GetUserByIDUseCase(get()) }
    single { GettingUsersLoggedInUseCase(get()) }
    single { FetchAllUsersUseCase(get()) }
    single <HashingPassword> { MD5Hasher() }
    single<AuthenticationRepository> { InMemoryAuthRepositoryImpl()  }
}
