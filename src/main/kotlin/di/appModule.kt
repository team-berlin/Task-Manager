package com.berlin.di


import org.koin.dsl.module


val appModule = module {
    includes(dataModule, repoModule, uiModule, useCaseModule)
}
