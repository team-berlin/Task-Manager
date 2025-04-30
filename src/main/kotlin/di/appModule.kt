package org.berlin.di

import com.berlin.data.memory.TaskRepositoryInMemory
import com.berlin.domain.model.*
import com.berlin.domain.repository.TaskRepository
import org.berlin.data.DummyData
import org.berlin.presentation.input_output.ConsoleReader
import org.berlin.presentation.input_output.ConsoleViewer
import org.berlin.presentation.input_output.Reader
import org.berlin.presentation.input_output.Viewer
import org.koin.dsl.module

val appModule = module {
    single { DummyData }
    single<MutableList<Task>> { DummyData.tasks }
    single<TaskRepository> { TaskRepositoryInMemory() }
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
}
