package com.berlin.di

import com.berlin.domain.model.User
import com.berlin.data.DummyData
import com.berlin.domain.usecase.authService.GetUserByIDUseCase
import com.berlin.domain.usecase.authService.GettingUsersLoggedInUseCase
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.authService.*
import com.berlin.presentation.task.*
import org.koin.core.qualifier.named
import org.koin.dsl.module


val uiModule = module {
    single<User>(named("currentUser")) { DummyData.users.first() }

    single { CreateTaskUI(get(), get(named("currentUser")), get(), get()) }
    single { AssignTaskUI(get(), get(), get(), get()) }
    single { DeleteTaskUI(get(), get(), get(),get()) }
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
                get<GetUserByIDUI>()
            ),
            viewer = get(),
            reader = get()
        )
    }
}