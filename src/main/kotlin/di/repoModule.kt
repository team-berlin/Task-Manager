package com.berlin.di

import com.berlin.data.audit.AuditRepositoryImpl
import com.berlin.data.mapper.*
import com.berlin.data.repository.AuthenticationRepositoryImpl
import com.berlin.data.repository.ProjectRepositoryImpl
import com.berlin.data.repository.TaskRepositoryImpl
import com.berlin.data.repository.TaskStateRepositoryImpl
import com.berlin.domain.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repoModule = module {

    single<ProjectRepository> {
        ProjectRepositoryImpl(
            get(named("ProjectDataSource")), get<ProjectMapper>()
        )
    }

    single<TaskRepository> {
        TaskRepositoryImpl(
            get(named("TaskDataSource")), get<TaskMapper>()
        )
    }

    single<AuditRepository> {
        AuditRepositoryImpl(
            get(named("AuditDataSource")), get<AuditLogMapper>()
        )
    }

    single<TaskStateRepository> {
        TaskStateRepositoryImpl(
            get(named("StateDataSource")), get(), get<TaskStateMapper>(), get<TaskMapper>()
        )
    }

    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(), get(named("UserDataSource")), get<UserMapper>()
        )
    }
}
