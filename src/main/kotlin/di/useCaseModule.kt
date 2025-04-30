package com.berlin.di

import com.berlin.domain.usecase.task.*
import org.koin.dsl.module

val useCaseModule = module {
    single { CreateTaskUseCase(get()) }
    single { AssignTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
}
