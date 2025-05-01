//package com.berlin
//
//import com.berlin.di.uiModule
//import com.berlin.di.useCaseModule
//import org.berlin.di.*
//import org.berlin.presentation.MainMenuUI
//import org.koin.core.context.startKoin
//import org.koin.mp.KoinPlatform.getKoin
//
//fun main() {
//
//    startKoin {
//        printLogger()
//        modules(appModule, useCaseModule, uiModule)
//    }
//
//    val mainMenu: MainMenuUI = getKoin().get()
//    mainMenu.run()
//}