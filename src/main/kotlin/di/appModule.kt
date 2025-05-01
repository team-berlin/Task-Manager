package com.berlin.di

import com.berlin.data.memory.TaskRepositoryInMemory
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.*
import com.berlin.domain.repository.TaskRepository
import com.berlin.data.DummyData
import com.berlin.presentation.io.ConsoleReader
import com.berlin.presentation.io.ConsoleViewer
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import org.koin.dsl.module

val appModule = module {
    single { DummyData }
    single<MutableList<Task>> { DummyData.tasks }
    single<TaskRepository> { TaskRepositoryInMemory() }
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
}
