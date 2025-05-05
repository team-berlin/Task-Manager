package com.berlin.di

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.memory.TaskRepositoryImpl
import com.berlin.data.project.ProjectRepositoryImpl
import com.berlin.data.schema.*
import com.berlin.domain.model.*
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.TaskRepository
import org.koin.dsl.module


val dataModule = module {

    single<BaseSchema<User>> {
        UserSchema(
            fileName = "user.csv",
            header = listOf("User Id", "UserName", "Password", "User Role")
        )
    }
    single<BaseSchema<Project>> {
        ProjectSchema(
            fileName = "project.csv",
            header = listOf("Project Id", "Project Name", "Description", "States", "Tasks")
        )
    }
    single<BaseSchema<AuditLog>> {
        AuditSchema(
            fileName = "audit.csv",
            header = listOf(
                "Audit Id",
                "Timestamp",
                "CreatedBy",
                "Audit Action",
                "Changes Description",
                "Entity Type",
                "Entity Id"
            )
        )
    }
    single<BaseSchema<State>> {
        StateSchema(fileName = "state.csv",
            header = listOf("State Id", "Name", "Project Id")) }
    single<BaseSchema<Task>> {
        TaskSchema(
            fileName = "task.csv",
            header = listOf(
                "Task Id",
                "Project Id",
                "Title",
                "Description",
                "State Id",
                "Assigned To User Id",
                "Create By User Id"
            )
        )
    }

    single<BaseDataSource<User>>{ CsvDataSource("csv_files", get()) }
    single<BaseDataSource<Project>>{ CsvDataSource("csv_files", get()) }
    single<BaseDataSource<Task>>{ CsvDataSource("csv_files", get()) }
    single<BaseDataSource<State>>{ CsvDataSource("csv_files", get()) }
    single<BaseDataSource<AuditLog>>{ CsvDataSource("csv_files", get()) }

    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }

}