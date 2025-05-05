package com.berlin.data.csv_data_source

import com.berlin.data.schema.ProjectSchema
import com.berlin.domain.model.Project

class ProjectCsvDataSource(rootDirectory:String,projectSchema: ProjectSchema)
    :CsvDataSource<Project>(rootDirectory,projectSchema) {
}