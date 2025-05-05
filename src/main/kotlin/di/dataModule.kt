package com.berlin.di

import com.berlin.data.Audit.AuditRepositoryImpl
import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.memory.TaskRepositoryImpl
import com.berlin.data.project.ProjectRepositoryImpl
import com.berlin.data.schema.*
import com.berlin.data.state.StateRepositoryImpl
import com.berlin.domain.model.*
import com.berlin.domain.repository.AuditRepository
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {

    single<BaseSchema<User>>(named("UserSchema")) {
    single<UserSchema> {
        UserSchema(
            fileName = "user.csv",
            header = listOf("User Id", "UserName", "Password", "User Role")
        )
    }
    single<BaseSchema<Project>>(named("ProjectSchema")) {
        ProjectSchema(
            fileName = "project.csv",
            header = listOf("Project Id", "Project Name", "Description", "States", "Tasks")
        )
    }
    single<BaseSchema<AuditLog>>(named("AuditSchema")) {
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
    single<BaseSchema<State>>(named("StateSchema")) {
        StateSchema(fileName = "state.csv",
            header = listOf("State Id", "Name", "Project Id")) }
    single<BaseSchema<Task>>(named("TaskSchema")) {
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

    single<BaseDataSource<User>>{ CsvDataSource("csv_files", get(named("UserSchema"))) }
    single<BaseDataSource<Project>>{ CsvDataSource("csv_files", get(named("ProjectSchema"))) }
    single<BaseDataSource<Task>>{ CsvDataSource("csv_files", get(named("TaskSchema"))) }
    single<BaseDataSource<State>>{ CsvDataSource("csv_files", get(named("StateSchema"))) }
    single<BaseDataSource<AuditLog>>{ CsvDataSource("csv_files", get(named("AuditSchema"))) }


    single <ProjectRepository> { ProjectRepositoryImpl(get()) }
    single <TaskRepository> { TaskRepositoryImpl(get()) }
    single <AuditRepository>{ AuditRepositoryImpl(get<BaseDataSource<AuditLog>>()) }
    single <StateRepository>{ StateRepositoryImpl(get<BaseDataSource<State>>(),get<BaseDataSource<Task>>()) }

}
}