package com.berlin

import com.berlin.di.uiModule
import com.berlin.di.useCaseModule
import com.berlin.di.*
import com.berlin.presentation.MainMenuUI
import com.berlin.presentation.authService.AuthenticateUserUi
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

fun main() {

    startKoin {
        printLogger()
        modules(appModule, useCaseModule, uiModule, dataModule)
    }

//    val mainMenu: MainMenuUI = getKoin().get()
//    mainMenu.run()

    val ui: AuthenticateUserUi = getKoin().get()
    ui.run()

}