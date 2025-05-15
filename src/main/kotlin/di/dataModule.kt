package com.berlin.di

import com.berlin.data.BaseDataSource
import com.berlin.data.mongodb.datasource.*
import com.berlin.data.dto.*
import com.berlin.data.mapper.*
import com.berlin.data.mongodb.config.MongoConfig
import com.berlin.domain.model.*
import com.berlin.domain.model.user.User
import com.berlin.domain.usecase.utils.hash_algorithm.HashingString
import com.berlin.domain.usecase.utils.hash_algorithm.MD5Hasher
import com.berlin.domain.usecase.utils.id_generator.IdGenerator
import com.berlin.domain.usecase.utils.id_generator.IdGeneratorImplementation
import data.AdminUserProvider
import data.UserCache
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


val dataModule = module {
    single { MongoConfig() }

    singleOf(::IdGeneratorImplementation) bind IdGenerator::class
    singleOf(::MD5Hasher) bind HashingString::class

    single<BaseDataSource<TaskDto>>(named(DatasourceQualifier.TASK_DATASOURCE)) { MongoDBTaskDataSource(get<MongoConfig>()) }
    single<BaseDataSource<TaskStateDto>>(named(DatasourceQualifier.TASK_STATE_DATASOURCE)) { MongoDBStateDataSource(get<MongoConfig>()) }
    single<BaseDataSource<ProjectDto>>(named(DatasourceQualifier.PROJECT_DATASOURCE)) { MongoDBProjectDataSource(get<MongoConfig>()) }
    single<BaseDataSource<AuditLogDto>>(named(DatasourceQualifier.AUDIT_LOG_DATASOURCE)) { MongoDBAuditLogDataSource(get<MongoConfig>()) }
    single<BaseDataSource<UserDto>>(named(DatasourceQualifier.USER_DATASOURCE)) { MongoDBUserDataSource(get<MongoConfig>()) }

    single { AdminUserProvider(get(named(DatasourceQualifier.USER_DATASOURCE)), get<UserMapper>()) }
    single { UserCache(get<AdminUserProvider>().load()) }

    single { TaskMapper() }.bind<EntityMapper<TaskDto, Task>>()
    single { ProjectMapper() }.bind<EntityMapper<ProjectDto, Project>>()
    single { TaskStateMapper() }.bind<EntityMapper<TaskStateDto, TaskState>>()
    single { UserMapper(get()) }.bind<EntityMapper<UserDto, User>>()
    single { AuditLogMapper() }.bind<EntityMapper<AuditLogDto, AuditLog>>()

}

object DatasourceQualifier {
    const val TASK_DATASOURCE = "mongoDbTaskDataSource"
    const val TASK_STATE_DATASOURCE = "mongoDbStateDataSource"
    const val PROJECT_DATASOURCE = "mongoDbProjectDataSource"
    const val AUDIT_LOG_DATASOURCE = "mongoDbAuditLogDataSource"
    const val USER_DATASOURCE = "mongoDbUserDataSource"

}