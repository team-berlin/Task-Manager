package com.berlin.di

import com.berlin.data.BaseDataSource
import com.berlin.data.audit.AuditRepositoryImpl
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.csv_data_source.schema.*
import com.berlin.data.dto.*
import com.berlin.data.mapper.*
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.data.mongodb.datasource.*
import com.berlin.data.repository.AuthenticationRepositoryImpl
import com.berlin.data.repository.ProjectRepositoryImpl
import com.berlin.data.repository.StateRepositoryImpl
import com.berlin.data.repository.TaskRepositoryImpl
import com.berlin.domain.model.*
import com.berlin.domain.model.user.User
import com.berlin.domain.repository.*
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.usecase.utils.hash_algorithm.MD5Hasher
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import data.AdminUserProvider
import data.UserCache
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val dataModule = module {
    single<IdGenerator> { IdGeneratorImplementation() }
    single<IdGeneratorImplementation> { IdGeneratorImplementation() }
    single<HashingString> { MD5Hasher() }

    single { AdminUserProvider(get(named("UserDtoDataSource")), get()) }
    single { UserCache(get<AdminUserProvider>().load()) }

    single<BaseSchema<UserDto>>(named("UserSchema")) {
        UserSchema(
            fileName = "user.csv", header = listOf("User Id", "UserName", "Password", "User Role")
        )
    }
    single<BaseSchema<ProjectDto>>(named("ProjectSchema")) {
        ProjectSchema(
            fileName = "project.csv", header = listOf("Project Id", "Project Name", "Description", "States", "Tasks")
        )
    }
    single<BaseSchema<AuditLogDto>>(named("AuditSchema")) {
        AuditSchema(
            fileName = "audit.csv", header = listOf(
                "Audit Id", "Timestamp", "CreatedBy", "Audit Action", "Changes Description", "Entity Type", "Entity Id"
            )
        )
    }
    single<BaseSchema<TaskStateDto>>(named("StateSchema")) {
        StateSchema(
            fileName = "state.csv", header = listOf("State Id", "Name", "Project Id")
        )
    }
    single<BaseSchema<TaskDto>>(named("TaskSchema")) {
        TaskSchema(
            fileName = "task.csv", header = listOf(
                "Task Id", "Project Id", "Title", "Description", "State Id", "Assigned To User Id", "Create By User Id"
            )
        )
    }

    single { MongoConfig() }

    single<BaseDataSource<Task>> { CsvDataSource("csv_files", get(named("TaskSchema"))) }
    single<BaseDataSource<TaskState>>(named("mongoDbStateDataSource")) { MongoDBStateDataSource(get<MongoConfig>()) }
    single<BaseDataSource<Project>>(named("mongoDbProjectDataSource")) { MongoDBProjectDataSource(get<MongoConfig>()) }
    single<BaseDataSource<AuditLog>>(named("mongoDbAuditLogDataSource")) { MongoDBauditLogDataSource(get<MongoConfig>()) }
    single<BaseDataSource<User>>(named("mongoDbUserDataSource")) { MongoDBUserDataSource(get<MongoConfig>()) }

    single<BaseDataSource<AuditLog>>(named("AuditDataSource")) { CsvDataSource("csv_files", get(named("AuditSchema"))) }

    single<BaseDataSource<User>>(named("UserDataSource")) { CsvDataSource("csv_files", get(named("UserSchema"))) }
    single<BaseDataSource<Project>>(named("ProjectDataSource")) {
        CsvDataSource(
            "csv_files", get(named("ProjectSchema"))
        )
    }

    single<BaseDataSource<UserDto>>(named("UserDtoDataSource")) {
        CsvDataSource("csv_files", get(named("UserSchema")))
    }

    single<BaseDataSource<Task>>(named("TaskDataSource")) { CsvDataSource("csv_files", get(named("TaskSchema"))) }
    single<BaseDataSource<TaskState>>(named("StateDataSource")) {
        CsvDataSource(
            "csv_files",
            get(named("StateSchema"))
        )
    }
    single<BaseDataSource<AuditLog>>(named("AuditDataSource")) { CsvDataSource("csv_files", get(named("AuditSchema"))) }

    single { TaskMapper() }.bind<EntityMapper<TaskDto, Task>>()
    single { ProjectMapper() }.bind<EntityMapper<ProjectDto, Project>>()
    single { TaskStateMapper() }.bind<EntityMapper<TaskStateDto, TaskState>>()
    single { UserMapper(get()) }.bind<EntityMapper<UserDto, User>>()
    single { AuditLogMapper() }.bind<EntityMapper<AuditLogDto, AuditLog>>()

    single<ProjectRepository> { ProjectRepositoryImpl(get(named("ProjectDataSource")), get<ProjectMapper>()) }
    single<TaskRepository> { TaskRepositoryImpl(get(named("TaskDataSource")), get<TaskMapper>()) }
    single<AuditRepository> { AuditRepositoryImpl(get(named("AuditDataSource")), get<AuditLogMapper>()) }
    single<StateRepository> {
        StateRepositoryImpl(
            get(named("StateDataSource")),
            get(),
            get<TaskStateMapper>(),
            get<TaskMapper>()
        )
    }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(),
            get(named("UserDataSource")),
            get<UserMapper>()
        )
    }
}