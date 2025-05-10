package com.berlin

import com.berlin.di.*
import com.berlin.presentation.MainMenuUI
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin

fun startApp() {
    if (org.koin.core.context.GlobalContext.getOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
}

fun main() {
    startApp()
    val mainMenuUi: MainMenuUI = getKoin().get()
    mainMenuUi.run()
}
