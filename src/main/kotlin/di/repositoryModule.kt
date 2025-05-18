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

val repositoryModule = module {

    single<ProjectRepository> {
        ProjectRepositoryImpl(
            get(named(DatasourceQualifier.PROJECT_DATASOURCE)), get<ProjectMapper>()
        )
    }

    single<TaskRepository> {
        TaskRepositoryImpl(
            get(named(DatasourceQualifier.TASK_DATASOURCE)), get<TaskMapper>()
        )
    }

    single<AuditRepository> {
        AuditRepositoryImpl(
            get(named(DatasourceQualifier.AUDIT_LOG_DATASOURCE)), get<AuditLogMapper>()
        )
    }

    single<TaskStateRepository> {
        TaskStateRepositoryImpl(
            get(named(DatasourceQualifier.TASK_STATE_DATASOURCE)),
            get(named(DatasourceQualifier.TASK_DATASOURCE)),
            get<TaskStateMapper>(),
            get<TaskMapper>()
        )
    }

    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            get(), get(named(DatasourceQualifier.USER_DATASOURCE)), get<UserMapper>()
        )
    }
}
