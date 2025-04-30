package com.berlin.di

import com.berlin.domain.model.User
import com.berlin.presentation.task.AssignTaskUI
import com.berlin.presentation.task.DeleteTaskUI
import com.berlin.presentation.task.GetTasksByProjectIdUI
import org.berlin.data.DummyData
import org.berlin.presentation.MainMenuUI
import org.berlin.presentation.task.CreateTaskUI
import org.koin.core.qualifier.named
import org.koin.dsl.module


val uiModule = module {
    single<User>(named("currentUser")) { DummyData.users.first() }

    single { CreateTaskUI(get(), get(named("currentUser")), get(), get()) }
    single { AssignTaskUI(get(), get(), get()) }
    single { DeleteTaskUI(get(), get(), get()) }
    single { GetTasksByProjectIdUI(get(), get()) }

    /* aggregated main menu */
    single {
        MainMenuUI(
            runners = listOf(
                get<CreateTaskUI>(),
                get<AssignTaskUI>(),
                get<DeleteTaskUI>(),
                get<GetTasksByProjectIdUI>()
            ),
            viewer = get(),
            reader = get()
        )
    }
}
