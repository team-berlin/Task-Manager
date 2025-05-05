package com.berlin.di

import com.berlin.data.DummyData
import com.berlin.domain.model.User
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.authService.AuthenticateUserUi
import com.berlin.presentation.authService.CreationOfMateUi
import com.berlin.presentation.authService.FetchAllUsersUI
import com.berlin.presentation.authService.GetUserByIDUI
import com.berlin.presentation.authService.GettingUsersLoggedInUI
import com.berlin.presentation.task.AssignTaskUI
import com.berlin.presentation.task.ChangeTaskStateUI
import com.berlin.presentation.task.CreateTaskUI
import com.berlin.presentation.task.DeleteTaskUI
import com.berlin.presentation.task.GetTaskByIdUI
import com.berlin.presentation.task.GetTasksByProjectIdUI
import com.berlin.presentation.task.UpdateTaskUI
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
    single { GetUserByIDUI(get(), get(), get()) }
    single { AuthenticateUserUi(get(), get(), get()) }
    single { CreationOfMateUi(get(), get(), get()) }
    single { GettingUsersLoggedInUI(get(), get()) }
    single { FetchAllUsersUI(get(),get()) }

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
                get<GetUserByIDUI>(),
                get<GettingUsersLoggedInUI>(),
                get<CreationOfMateUi>(),
                get<AuthenticateUserUi>(),
                get<FetchAllUsersUI>()
            ),
            viewer = get(),
            reader = get()
        )
    }
}