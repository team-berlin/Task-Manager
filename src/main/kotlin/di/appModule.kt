package com.berlin.di


import com.berlin.domain.hashPassword.HashingPassword
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.helper.IdGenerator
import com.berlin.presentation.io.ConsoleReader
import com.berlin.presentation.io.ConsoleViewer
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import org.koin.dsl.module


val appModule = module {
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
    single<IdGenerator> { IdGeneratorImplementation() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
    single <HashingPassword> { MD5Hasher() }
}
