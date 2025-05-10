package com.berlin.di

import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.schema.*
import com.berlin.data.repository.*
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.usecase.utils.hash_algorithm.MD5Hasher
import com.berlin.domain.model.*
import com.berlin.domain.repository.*
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import data.UserCache
import org.koin.core.qualifier.named
import org.koin.dsl.module



val dataModule = module {
    single<IdGenerator> { IdGeneratorImplementation() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
    single <HashingString> { MD5Hasher() }

    single {
        UserCache(
            User("user1234", "admin", "1212", UserRole.ADMIN)
        )
    }

    single<BaseSchema<User>>(named("UserSchema")) {
        UserSchema(
            fileName = "user.csv", header = listOf("User Id", "UserName", "Password", "User Role")
        )
    }
    single<BaseSchema<Project>>(named("ProjectSchema")) {
        ProjectSchema(
            fileName = "project.csv", header = listOf("Project Id", "Project Name", "Description", "States", "Tasks")
        )
    }
    single<BaseSchema<AuditLog>>(named("AuditSchema")) {
        AuditSchema(
            fileName = "audit.csv", header = listOf(
                "Audit Id", "Timestamp", "CreatedBy", "Audit Action", "Changes Description", "Entity Type", "Entity Id"
            )
        )
    }
    single<BaseSchema<TaskState>>(named("StateSchema")) {
        StateSchema(
            fileName = "state.csv", header = listOf("State Id", "Name", "Project Id")
        )
    }
    single<BaseSchema<Task>>(named("TaskSchema")) {
        TaskSchema(
            fileName = "task.csv", header = listOf(
                "Task Id", "Project Id", "Title", "Description", "State Id", "Assigned To User Id", "Create By User Id"
            )
        )
    }

    single<BaseDataSource<User>>(named("UserDataSource")) { CsvDataSource("csv_files", get(named("UserSchema"))) }
    single<BaseDataSource<Project>>(named("ProjectDataSource")) {
        CsvDataSource(
            "csv_files", get(named("ProjectSchema"))
        )
    }
    single<BaseDataSource<Task>>(named("TaskDataSource")) { CsvDataSource("csv_files", get(named("TaskSchema"))) }
    single<BaseDataSource<TaskState>>(named("StateDataSource")) { CsvDataSource("csv_files", get(named("StateSchema"))) }
    single<BaseDataSource<AuditLog>>(named("AuditDataSource")) { CsvDataSource("csv_files", get(named("AuditSchema"))) }



    single<ProjectRepository> { ProjectRepositoryImpl(get(named("ProjectDataSource"))) }
    single<TaskRepository> { TaskRepositoryImpl(get(named("TaskDataSource"))) }
    single<AuditRepository> { AuditRepositoryImpl(get(named("AuditDataSource"))) }
    single<StateRepository> { StateRepositoryImpl(get(named("StateDataSource")), get(named("TaskDataSource"))) }
    single<AuthenticationRepository> { AuthenticationRepositoryImpl(get(), get(named("UserDataSource"))) }
}