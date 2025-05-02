package com.berlin.di

import com.berlin.domain.model.User
import com.berlin.data.DummyData
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.UiRunner
import com.berlin.presentation.authService.AuthenticateUserUi
import com.berlin.presentation.authService.GetUserByIDUI
import com.berlin.presentation.task.*
import org.koin.core.qualifier.named
import org.koin.dsl.module


val uiModule = module {
    single<User>(named("currentUser")) { DummyData.users.first() }

    single { CreateTaskUI(get(), get(named("currentUser")), get(), get()) }
    single { AssignTaskUI(get(), get(), get()) }
    single { DeleteTaskUI(get(), get(), get()) }
    single { GetTasksByProjectIdUI(get(), get(), get()) }
    single { UpdateTaskUI(get(), get(), get()) }
    single { ChangeTaskStateUI(get(), get(), get()) }
    single { GetTaskByIdUI(get(), get(), get()) }
    single { GetUserByIDUI(get(), get(), get()) }
    single { AuthenticateUserUi(get(), get(), get()) }
    single<List<UiRunner>>(named("adminRunners")) {
        listOf(
            get<CreateTaskUI>(),
            get<AssignTaskUI>(),
            get<DeleteTaskUI>(),
            get<UpdateTaskUI>(),
            get<GetTaskByIdUI>(),
            get<GetUserByIDUI>()
        )
    }
    single<List<UiRunner>>(named("mateRunners")) {
        listOf(
            get<GetTasksByProjectIdUI>(),
            get<ChangeTaskStateUI>()
        )
    }

    single {
        MainMenuUI(
            logInUI = get(),
            viewer = get(),
            reader = get(),
            adminRunners = get(named("adminRunners")),
            mateRunners = get(named("mateRunners"))
        )
    }
}