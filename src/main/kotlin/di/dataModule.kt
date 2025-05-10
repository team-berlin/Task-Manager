package com.berlin.di
import com.berlin.data.BaseDataSource
import com.berlin.data.BaseSchema
import com.berlin.data.authentication.AuthenticationRepositoryImpl
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.data.mongodb.datasource.*
import com.berlin.data.task.TaskRepositoryImpl
import com.berlin.data.project.ProjectRepositoryImpl
import com.berlin.data.schema.*
import com.berlin.data.state.StateRepositoryImpl
import com.berlin.domain.model.*
import com.berlin.domain.repository.AuthenticationRepository
import com.berlin.domain.repository.ProjectRepository
import com.berlin.domain.repository.StateRepository
import com.berlin.domain.repository.TaskRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataModule = module {

    single<BaseSchema<User>>(named("UserSchema")) {
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

    single { MongoConfig() }

    single<BaseDataSource<Task>>(named("mongoDbTaskDataSource")) { MongoDBTaskDataSource(get()) }
    single<BaseDataSource<State>>(named("mongoDbStateDataSource")) { MongoDBStateDataSource(get()) }
    single<BaseDataSource<Project>>(named("mongoDbProjectDataSource")) { MongoDBProjectDataSource(get()) }
    single<BaseDataSource<AuditLog>>(named("mongoDbAuditLogDataSource")) { MongoDBauditLogDataSource(get()) }
    single<BaseDataSource<User>>(named("mongoDbUserDataSource")) { MongoDBUserDataSource(get()) }

    single<BaseDataSource<AuditLog>>(named("AuditDataSource")){ CsvDataSource("csv_files", get(named("AuditSchema"))) }

    single<BaseDataSource<User>>(named("UserDataSource")){ CsvDataSource("csv_files", get(named("UserSchema"))) }
    single<BaseDataSource<Project>>(named("ProjectDataSource")){ CsvDataSource("csv_files", get(named("ProjectSchema"))) }
    single<BaseDataSource<Task>>(named("TaskDataSource")){ CsvDataSource("csv_files", get(named("TaskSchema"))) }
    single<BaseDataSource<State>>(named("StateDataSource")){ CsvDataSource("csv_files", get(named("StateSchema"))) }
    single<BaseDataSource<AuditLog>>(named("AuditDataSource")){ CsvDataSource("csv_files", get(named("AuditSchema"))) }



    single <ProjectRepository> { ProjectRepositoryImpl(get(named("ProjectDataSource"))) }
    single <TaskRepository> { TaskRepositoryImpl(get(named("TaskDataSource"))) }
//    single <AuditRepository>{ AuditRepositoryImpl(get(named("AuditDataSource"))) }
    single <StateRepository>{ StateRepositoryImpl(get(named("StateDataSource")),get(named("TaskDataSource"))) }
    single <AuthenticationRepository> { AuthenticationRepositoryImpl(get(named("UserDataSource"))) }

    single <AuthenticationRepository>{ AuthenticationRepositoryImpl(get(named("UserDataSource"))) }

}