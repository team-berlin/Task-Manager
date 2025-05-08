package com.berlin.di

import com.berlin.data.DummyData
import com.berlin.domain.model.User
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.authService.*
import com.berlin.presentation.state.*
import com.berlin.presentation.project.*
import com.berlin.presentation.task.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.math.sin


val uiModule = module {
    single<User>(named("currentUser")) { DummyData.users.first() }

    single { CreateTaskUI(get(), get(named("currentUser")), get(), get()) }
    single { AssignTaskUI(get(), get(), get(), get()) }
    single { DeleteTaskUI(get(), get(), get(), get()) }
    single { GetTasksByProjectIdUI(get(), get(), get()) }
    single { UpdateTaskUI(get(), get(), get(), get()) }
    single { ChangeTaskStateUI(get(), get(), get(), get()) }
    single { GetTaskByIdUI(get(), get(), get()) }
    single { GetUserByIDUseCase(get()) }
    single { GettingUsersLoggedInUI(get(), get()) }

    single { CreationOfMateUi(get(),get(),get()) }
    single { AuthenticateUserUi(get(),get(),get()) }
    single { FetchAllUsersUI(get(),get()) }
    single { GetUserByIDUI(get(),get(),get()) }
    single { CreateProjectUi(get(),get(),get()) }
    single { DeleteProjectUi(get(),get(),get(),get()) }
    single { GetAllProjectsUi(get(),get()) }
    single { GetProjectByIdUi(get(),get(),get()) }
    single { UpdateProjectUi(get(),get(),get(),get(),get()) }

    single { CreateStateUi(get(), get(), get()) }
    single { DeleteStateUi(get(), get(), get(), get()) }
    single { GetStateByIdUi(get(), get(), get()) }
    single { UpdateStateUi(get(), get(), get(), get()) }
    single { GetAllStatesByProjectIdUi(get(),get(),get()) }
    /* aggregated main menu */
    single {
        MainMenuUI(
            runners = listOf(
                get<CreateTaskUI>(),
                get<AssignTaskUI>(),
                get<DeleteTaskUI>(),
                get<GetTasksByProjectIdUI>(),
                get<UpdateTaskUI>(),
                get<ChangeTaskStateUI>(),
                get<GetTaskByIdUI>(),

                get<AuthenticateUserUi>(),
                get<CreationOfMateUi>(),
                get<FetchAllUsersUI>(),
                get<GettingUsersLoggedInUI>(),
                get<GetUserByIDUI>(),

                get<CreateStateUi>(),
                get<DeleteStateUi>(),
                get<GetStateByIdUi>(),
                get<UpdateStateUi>(),
                get<GetAllStatesByProjectIdUi>()
                
                get<CreateProjectUi>(),
                get<DeleteProjectUi>(),
                get<GetAllProjectsUi>(),
                get<GetProjectByIdUi>(),
                get<UpdateProjectUi>(),
            ),
            viewer = get(),
            reader = get()
        )
    }
}