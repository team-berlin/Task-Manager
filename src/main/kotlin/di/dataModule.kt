package com.berlin.di

import com.berlin.data.BaseSchema
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.csv_data_source.ProjectCsvDataSource
import com.berlin.data.schema.*
import com.berlin.domain.model.*
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
    single<BaseSchema<State>> { StateSchema(fileName = "state.csv", header = listOf("State Id", "Name", "Project Id")) }
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

    single { CsvDataSource<User>("csv_files", get()) }
    single { CsvDataSource<Project>("csv_files", get()) }
    single { CsvDataSource<Task>("csv_files", get()) }
    single { CsvDataSource<State>("csv_files", get()) }
    single { CsvDataSource<AuditLog>("csv_files", get()) }


    single { ProjectCsvDataSource("csv_files",get()) }
}