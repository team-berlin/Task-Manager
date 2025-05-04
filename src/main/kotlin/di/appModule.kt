package com.berlin.di

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.berlin.data.memory.TaskRepositoryImpl
import com.berlin.domain.helper.IdGeneratorImplementation
import com.berlin.domain.model.*
import com.berlin.domain.repository.TaskRepository
import com.berlin.data.DummyData
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.presentation.io.ConsoleReader
import com.berlin.presentation.io.ConsoleViewer
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer
import org.koin.dsl.module

val appModule = module {

    single<BaseDataSource<Task>> {
        CsvDataSource(
            rootDirectory = "csv_files",
            schema = get<BaseSchema<Task>>()
        )
    }
    single<TaskRepository> {
        TaskRepositoryImpl( get<BaseDataSource<Task>>() )
    }
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
}
