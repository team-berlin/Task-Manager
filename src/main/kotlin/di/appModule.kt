package com.berlin.di


import com.berlin.domain.hashPassword.HashingString
import com.berlin.domain.hashPassword.MD5Hasher
import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.domain.usecase.utils.IDGenerator.IdGeneratorImplementation
import com.berlin.domain.usecase.utils.IDGenerator.IdGenerator
import com.berlin.presentation.io.ConsoleReader
import com.berlin.presentation.io.ConsoleViewer
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import data.UserCache
import org.koin.dsl.module


val appModule = module {
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
    single<IdGenerator> { IdGeneratorImplementation() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
    single <HashingString> { MD5Hasher() }

    single {
        UserCache(
            User("user1234", "admin", "1212", UserRole.ADMIN)
        )
    }

}
