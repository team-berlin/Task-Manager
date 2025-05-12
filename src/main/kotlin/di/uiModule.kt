package com.berlin.di

import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.CategoryUI
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.UiRunner
import com.berlin.presentation.audit.AuditByProjectUI
import com.berlin.presentation.audit.AuditByTaskUI
import com.berlin.presentation.audit.AuditByUserUI
import com.berlin.presentation.authService.*
import com.berlin.presentation.io.ConsoleReader
import com.berlin.presentation.io.ConsoleViewer
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import com.berlin.presentation.project.*
import com.berlin.presentation.state.*
import com.berlin.presentation.task.*
import data.UserCache
import org.koin.core.qualifier.named
import org.koin.dsl.module

val uiModule = module {
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
    single { CreateTaskUI(get(), get(), get(), get(), get(), get(), get()) }
    single { AssignTaskUI(get(), get(), get(), get(), get()) }
    single { DeleteTaskUI(get(), get(), get(), get()) }
    single { GetTasksByProjectIdUI(get(), get(), get(), get(), get()) }
    single { UpdateTaskUI(get(), get(), get(), get(), get()) }
    single { ChangeTaskStateUI(get(), get(), get(), get(), get()) }
    single { GetTaskByIdUI(get(), get(), get()) }

    single { CreateProjectUi(get(), get(), get()) }
    single { DeleteProjectUi(get(), get(), get(), get()) }
    single { GetAllProjectsUi(get(), get()) }
    single { GetProjectByIdUi(get(), get(), get()) }
    single { UpdateProjectUi(get(), get(), get(), get(), get()) }

    single { CreateStateUI(get(), get(), get(), get()) }
    single { DeleteStateUi(get(), get(), get(), get()) }
    single { GetAllStatesByProjectIdUI(get(), get(), get(), get()) }
    single { GetStateByIdUi(get(), get(), get()) }
    single { UpdateStateUI(get(), get(), get(), get()) }

    single { AuditByProjectUI(get(), get(), get(), get()) }
    single { AuditByTaskUI(get(), get(), get(), get(), get()) }
    single { AuditByUserUI(get(), get(), get(), get()) }

    single { GettingUsersLoggedInUI(get(), get()) }
    single { CreateMateUI(get(), get(), get()) }
    single { FetchAllUsersUI(get(), get()) }
    single { GetUserByIDUI(get(), get(), get()) }
    single { GetUserByIDUseCase(get()) }
    single { AuthenticateUserUI(get(), get(), get()) }

    single<UiRunner>(named("tasksCategory")) {
        CategoryUI(
            id = 1, label = "Tasks", children = listOf(
                get<CreateTaskUI>(),
                get<AssignTaskUI>(),
                get<DeleteTaskUI>(),
                get<GetTasksByProjectIdUI>(),
                get<UpdateTaskUI>(),
                get<ChangeTaskStateUI>(),
                get<GetTaskByIdUI>()
            ), viewer = get<Viewer>(), reader = get<Reader>(), userCache = get()
        )
    }

    single<UiRunner>(named("projectsCategory")) {
        CategoryUI(
            id = 2, label = "Projects", children = listOf(
                get<CreateProjectUi>(),
                get<DeleteProjectUi>(),
                get<GetAllProjectsUi>(),
                get<GetProjectByIdUi>(),
                get<UpdateProjectUi>()
            ), viewer = get<Viewer>(), reader = get<Reader>(), userCache = get()
        )
    }

    single<UiRunner>(named("statesCategory")) {
        CategoryUI(
            id = 3, label = "States", children = listOf(
                get<CreateStateUI>(),
                get<DeleteStateUi>(),
                get<GetAllStatesByProjectIdUI>(),
                get<GetStateByIdUi>(),
                get<UpdateStateUI>()
            ), viewer = get<Viewer>(), reader = get<Reader>(), userCache = get()
        )
    }

    single<UiRunner>(named("auditCategory")) {
        CategoryUI(
            id = 4, label = "Audit Logs", children = listOf(
                get<AuditByProjectUI>(), get<AuditByTaskUI>(), get<AuditByUserUI>()
            ), viewer = get<Viewer>(), reader = get<Reader>(), userCache = get()
        )
    }

    single<UiRunner>(named("usersCategory")) {
        CategoryUI(
            id = 5, label = "Users", children = listOf(
                get<CreateMateUI>(), get<FetchAllUsersUI>(), get<GettingUsersLoggedInUI>(), get<GetUserByIDUI>()
            ),viewer = get<Viewer>(), reader = get<Reader>(), userCache = get()
        )
    }

    single {
        MainMenuUI(
            runners = listOf(
                get(named("tasksCategory")),
                get(named("projectsCategory")),
                get(named("statesCategory")),
                get(named("auditCategory")),
                get(named("usersCategory"))
            ), viewer = get<Viewer>(), reader = get<Reader>(), authUi = get<AuthenticateUserUI>(), userCache = get<UserCache>()
        )
    }
}
