package com.berlin.di

import com.berlin.data.csv.AuthRepositoryCSV
import com.berlin.data.csv.AuditRepositoryCSV
import com.berlin.data.csv.ProjectRepositoryCSV
import com.berlin.data.csv.StateRepositoryCSV
import com.berlin.data.csv.TaskRepositoryCSV
import com.berlin.logic.repositories.AuditRepository
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.repositories.ProjectRepository
import com.berlin.logic.repositories.StateRepository
import com.berlin.logic.repositories.TaskRepository
import com.berlin.logic.usecases.task.AssignTaskUseCase
import com.berlin.logic.usecases.task.CreateTaskUseCase
import com.berlin.logic.usecases.task.DeleteTaskUseCase
import com.berlin.logic.usecases.task.UpdateTaskUseCase
import com.berlin.ui.TaskUI
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthenticationRepository> {
        AuthRepositoryCSV()
    }

    single<AuditRepository> {
        AuditRepositoryCSV()
    }

    single<ProjectRepository> {
        ProjectRepositoryCSV()
    }

    single<StateRepository> {
        StateRepositoryCSV()
    }

    single<TaskRepository> {
        TaskRepositoryCSV(
            userRepository = get(),
            auditRepository = get()
        )
    }
}

val useCaseModule = module {
    factory { CreateTaskUseCase(get(), get(), get()) }
    factory { UpdateTaskUseCase(get(), get()) }
    factory { DeleteTaskUseCase(get(), get(), get()) }
    factory { AssignTaskUseCase(get(), get()) }
}

val uiModule = module {
    factory {
        TaskUI(
            taskRepository = get(),
            projectRepository = get(),
            stateRepository = get(),
            authRepository = get(),
            createTaskUseCase = get(),
            updateTaskUseCase = get(),
            deleteTaskUseCase = get(),
            assignTaskUseCase = get()
        )
    }
}

val appModules = listOf(repositoryModule, useCaseModule, uiModule)