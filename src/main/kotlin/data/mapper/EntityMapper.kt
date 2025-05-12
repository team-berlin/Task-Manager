package com.berlin.data.mapper

interface EntityMapper<Data, Domain> {
    fun mapToDomainModel(from: Data): Domain
    fun mapToDataModel(from: Domain): Data
}