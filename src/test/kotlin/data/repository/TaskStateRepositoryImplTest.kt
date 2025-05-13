package com.berlin.data.repository

import com.berlin.data.BaseDataSource
import com.berlin.data.dto.TaskDto
import com.berlin.data.dto.TaskStateDto
import com.berlin.data.mapper.TaskMapper
import com.berlin.data.mapper.TaskStateMapper
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.StateNotFoundException
import com.berlin.domain.model.Task
import com.berlin.domain.model.TaskState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskStateRepositoryImplTest {
    private lateinit var repository: TaskStateRepositoryImpl
    private lateinit var taskStateMapper: TaskStateMapper
    private lateinit var taskMapper: TaskMapper
    private val stateDataSource: BaseDataSource<TaskStateDto> = mockk()
    private val taskDataSource: BaseDataSource<TaskDto> = mockk()

    @BeforeEach
    fun setUp() {
        taskStateMapper = mockk()
        taskMapper= mockk()
        repository = TaskStateRepositoryImpl(stateDataSource, taskDataSource,
            taskStateMapper, taskMapper)
    }

    // region addState

    @Test
    fun `addState should return State created successfully when added succeeds`() {
        // Given
        every { taskStateMapper.mapToDataModel(validState) } returns validStateDto
        every { stateDataSource.write(validStateDto) } returns true

        // When
        val result = repository.addState(validState)

        // Then
        assertThat(result).isEqualTo("State created successfully")
    }

    @Test
    fun `addState should return InvalidStateException when added fails`() {
        // Given
        every { taskStateMapper.mapToDataModel(invalidState) } returns invalidStateDto
        every { stateDataSource.write(any()) } returns false

        // When & Then
       assertThrows<InvalidStateException> { repository.addState(invalidState) }
    }
    // endregion

    // region getStateById

    @Test
    fun `getStateById should return state when data source returns state`() {
        // Given
        every { stateDataSource.getById(any()) } returns validStateDto
        every { taskStateMapper.mapToDomainModel(validStateDto) } returns validState

        // When
        val result = repository.getStateById(validState.id)

        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateById should return null when data source returns StateNotFoundException exception`() {
        // Given
        every { stateDataSource.getById(any()) } returns null
        every { taskStateMapper.mapToDomainModel(validStateDto) } returns validState

        // When & Then
        assertThrows<StateNotFoundException> { repository.getStateById(validState.id) }
    }
    // endregion


    // region getStatesByProjectId

    @Test
    fun `getStatesByProjectId should return list of states match project id`() {
        // Given
        every { stateDataSource.getAll() } returns listOf(validStateDto)
        every { taskStateMapper.mapToDomainModel(validStateDto) } returns validState

        // When
        val result = repository.getStatesByProjectId(validState.projectId)

        // Then
        assertThat(result).isEqualTo(listOf(validState))
    }

    @Test
    fun `getStatesByProjectId should return empty when data source returns empty list or no matches project`() {
        // Given
        every { stateDataSource.getAll() } returns emptyList()
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
        every { taskDataSource.getAll() } returns listOf(validTaskDto)
        every { taskMapper.mapToDomainModel(validTaskDto) } returns task
        // When
        val result = repository.getTasksByStateId(validState.id)
        // Then
        assertThat(result).isEqualTo(listOf(task))
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
    fun `updateState should return Updated Successfully when update succeeds`() {
        // Given
        every { taskStateMapper.mapToDataModel(validState) } returns validStateDto
        every { stateDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result).isEqualTo("Updated Successfully")
    }

    @Test
    fun `updateState should return InvalidStateException when update fails`() {
        // Given
        every { taskStateMapper.mapToDataModel(invalidState) } returns invalidStateDto
        every { stateDataSource.update(any(), any()) } returns false

        // When & Then
        assertThrows<InvalidStateException> { repository.updateState(invalidState) }
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
    fun `deleteState should return InvalidStateException exception when delete fails`() {
        // Given
        every { stateDataSource.delete(any()) } returns false
        // When & Then
        assertThrows<InvalidStateException> { repository.deleteState(invalidState.id) }
    }

    // endregion


    // region getStateByTaskId
    @Test
    fun `getStateByTaskId should return state when data sources returns state matches task`() {
        // Given
        every { taskDataSource.getById(any()) } returns validTaskDto
        every { stateDataSource.getById(any()) } returns validStateDto
        every { taskStateMapper.mapToDomainModel(validStateDto) } returns validState

        // When
        val result = repository.getStateByTaskId(task.id)

        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateByTaskId should return null when data sources returns null no match state and no task`() {
        // Given
        every { taskMapper.mapToDataModel(invalidtask) } returns invalidtaskDto
        every { taskDataSource.getById(any()) } returns null
        every { stateDataSource.getById(any()) } returns null
        // When & Then
        val result=repository.getStateByTaskId(invalidtask.id)
        assertThat(result).isNull()
    }
    // endregion


    //region getAllStates
    @Test
    fun `getAllStates should return states when there is states stored`(){
        //Given
        every { stateDataSource.getAll() } returns listOf(validStateDto)
        every { taskStateMapper.mapToDomainModel(validStateDto) } returns validState

        //when
        val result=repository.getAllStates()

        //Then
        assertThat(result).isEqualTo(listOf(validState))
    }

    @Test
    fun `getAllStates should return null when there is no states`(){
        //Given
        every { stateDataSource.getAll() } returns emptyList()

        //when
        val result=repository.getAllStates()

        //Then
        assertThat(result).isEmpty()

    }
    //endregion

    companion object {
        val invalidStateDto=TaskStateDto(
            id = "st123", name = "Doing", projectId = "pppppp"
        )
        val invalidState=TaskState(
        id = "st123", name = "Doing", projectId = "pppppp"
        )

        val validStateDto = TaskStateDto(
            id = "st123", name = "ToDo", projectId = "pppppp"
        )

        val validState = TaskState(
            id = "st123", name = "ToDo", projectId = "pppppp"
        )
        val invalidtask=Task(
            id = "invalid123",
            projectId = "hhhhh",
            title = "zzzz",
            description = null,
            stateId = "st123",
            assignedToUserId = "57r",
            createByUserId = "r444"
        )
        val invalidtaskDto=TaskDto(
            id = "invalid123",
            projectId = "hhhhh",
            title = "zzzz",
            description = null,
            stateId = "st123",
            assignedToUserId = "57r",
            createByUserId = "r444"
        )
        val task= Task(
            id = "t6665",
            projectId = "hhhhh",
            title = "zzzz",
            description = null,
            stateId = "st123",
            assignedToUserId = "57r",
            createByUserId = "r444"
        )
        val validTaskDto = TaskDto(
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