package com.berlin.data.state

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskDto
import com.berlin.data.dto.TaskStateDto
import com.berlin.data.mapper.TaskMapper
import com.berlin.data.mapper.TaskStateMapper
import com.berlin.data.repository.StateRepositoryImpl
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.model.TaskState
import com.berlin.domain.model.Task
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskStateRepositoryImplTest {
    private lateinit var repository: StateRepositoryImpl
    private val stateDataSource: BaseDataSource<TaskStateDto> = mockk()
    private val taskDataSource: BaseDataSource<TaskDto> = mockk()


    @BeforeEach
    fun setUp() {
        val taskStateMapper: TaskStateMapper = mockk()
        val taskMapper: TaskMapper = mockk()
        repository = StateRepositoryImpl(stateDataSource, taskDataSource,
            taskStateMapper, taskMapper)
    }

    // region addState

    @Test
    fun `addState should return success when added succeeds`() {
        // Given
        every { stateDataSource.write(any()) } returns true
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result).isEqualTo("State created successfully")
    }

    @Test
    fun `addState should return success with state id when added succeeds`() {
        // Given
        every { stateDataSource.write(any()) } returns true
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result).isEqualTo(validState.id)
    }

    @Test
    fun `addState should return failure with InvalidStateException when added fails`() {
        // Given
        every { stateDataSource.write(any()) } returns false
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region getStateById

    @Test
    fun `getStateById should return state when data source returns state`() {
        // Given
        every { stateDataSource.getById(any()) } returns validStateDto
        // When
        val result = repository.getStateById(validState.id)
        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateById should return null when data source returns null`() {
        // Given
        every { stateDataSource.getById(any()) } returns null
        // When
        val result = repository.getStateById(validState.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    // region getStatesByProjectId
    @Test
    fun `getStatesByProjectId should return list of states match this project`() {
        // Given
        every { stateDataSource.getAll() } returns listOf()
        // When
        val result = repository.getStatesByProjectId(validState.projectId)
        // Then
        assertThat(result).isEqualTo(states)
    }

    @Test
    fun `getStatesByProjectId should null when data source returns empty list or no matches project`() {
        // Given
        every { stateDataSource.getAll() } returns states
        // When
        val result = repository.getStatesByProjectId(validState.projectId)
        // Then
        assertThat(result).isEmpty()
    }
    // endregion

    // region getTasksByStateId
    @Test
    fun `getTasksByStateId should return list of matches tasks when data source returns tasks`() {
        // Given
        every { taskDataSource.getAll() } returns tasks
        // When
        val result = repository.getTasksByStateId(validState.id)
        // Then
        assertThat(result).isEqualTo(tasks)
    }

    @Test
    fun `getTasksByStateId should null when data source returns empty list or no matches tasks`() {
        // Given
        every { taskDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getTasksByStateId(validState.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    // region updateState
    @Test
    fun `updateState should return success when update succeeds`() {
        // Given
        every { stateDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `updateState should return success with state id when update succeeds`() {
        // Given
        every { stateDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result).isEqualTo(validState.id)
    }

    @Test
    fun `updateState should return failure when update fails`() {
        // Given
        every { stateDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result).isEqualTo("can not update state")
    }

    @Test
    fun `updateState should return failure with InvalidStateException when update fails`() {
        // Given
        every { stateDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region deleteState
    @Test
    fun `deleteState should return success when delete succeeds`() {
        // Given
        every { stateDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `deleteState should return success with state id when delete succeeds`() {
        // Given
        every { stateDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result).isEqualTo(validState.id)
    }

    @Test
    fun `deleteState should return failure when delete fails`() {
        // Given
        every { stateDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result).isEqualTo("can not delete state")
    }

    @Test
    fun `deleteState should return failure with InvalidStateException when delete fails`() {
        // Given
        every { stateDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region getStateByTaskId
    @Test
    fun `getStateByTaskId should return state when data sources returns state matches task`() {
        // Given
        every { taskDataSource.getById(any()) } returns validTask
        every { stateDataSource.getById(any()) } returns validStateDto
        // When
        val result = repository.getStateByTaskId(validTask.id)
        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateByTaskId should return null when data sources returns null no match state and no task`() {
        // Given
        every { taskDataSource.getById(any()) } returns null
        every { stateDataSource.getById(any()) } returns null
        // When
        val result = repository.getStateByTaskId(validTask.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    companion object {

        val validStateDto = TaskStateDto(
            id = "st123", name = "ToDo", projectId = "pppppp"
        )

        val validState = TaskState(
            id = "st123", name = "ToDo", projectId = "pppppp"
        )
        val validTask = TaskDto(
            id = "t6665",
            projectId = "hhhhh",
            title = "zzzz",
            description = null,
            stateId = "st123",
            assignedToUserId = "57r",
            createByUserId = "r444"
        )
        val states = listOf(
            TaskStateDto(id = "st123", name = "ToDo", projectId = "pppppp"),
            TaskStateDto(id = "st456", name = "InProgress", projectId = "pppppp"),
            TaskStateDto(id = "st789", name = "Done", projectId = "pppppp")
        )
        val tasks = listOf(
            TaskDto(
                id = "t6665",
                projectId = "hhhhh",
                title = "zzzz",
                description = null,
                stateId = "st123",
                assignedToUserId = "57r",
                createByUserId = "r444"
            ), TaskDto(
                id = "t4576",
                projectId = "p657",
                title = "Task1",
                description = null,
                stateId = "st123",
                assignedToUserId = "u66",
                createByUserId = "t4"
            ), TaskDto(
                id = "t897",
                projectId = "p4566",
                title = "Task2",
                description = null,
                stateId = "st123",
                assignedToUserId = "y66",
                createByUserId = "l99"
            ), TaskDto(
                id = "t3555",
                projectId = "p45",
                title = "Task3",
                description = null,
                stateId = "st123",
                assignedToUserId = "o99",
                createByUserId = "3w"
            )
        )
    }
}