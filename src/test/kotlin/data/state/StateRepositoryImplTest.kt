package com.berlin.data.state

import com.berlin.data.BaseDataSource
import com.berlin.data.csv_data_source.CsvDataSource
import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.model.State
import com.berlin.domain.model.Task
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StateRepositoryImplTest {
    private lateinit var repository: StateRepositoryImpl
    private val stateDataSource: BaseDataSource<State> = mockk()
    private val taskDataSource: BaseDataSource<Task> = mockk()

    @BeforeEach
    fun setUp() {
        repository = StateRepositoryImpl(stateDataSource, taskDataSource)
    }

    // region addState

    @Test
    fun `addState should return success when added succeeds`() = runTest {
        // Given
        coEvery { stateDataSource.write(any()) } returns true
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `addState should return success with state id when added succeeds`()= runTest  {
        // Given
        coEvery { stateDataSource.write(any()) } returns true
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validState.id)
    }

    @Test
    fun `addState should return failure when added fails`() = runTest {
        // Given
        coEvery { stateDataSource.write(any()) } returns false
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `addState should return failure with InvalidStateException when added fails`() = runTest {
        // Given
        coEvery { stateDataSource.write(any()) } returns false
        // When
        val result = repository.addState(validState)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region getStateById

    @Test
    fun `getStateById should return state when data source returns state`() = runTest {
        // Given
        coEvery { stateDataSource.getById(any()) } returns validState
        // When
        val result = repository.getStateById(validState.id)
        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateById should return null when data source returns null`() = runTest {
        // Given
        coEvery { stateDataSource.getById(any()) } returns null
        // When
        val result = repository.getStateById(validState.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    // region getStatesByProjectId
    @Test
    fun `getStatesByProjectId should return list of states match this project`() = runTest {
        // Given
        coEvery { stateDataSource.getAll() } returns states
        // When
        val result = repository.getStatesByProjectId(validState.projectId)
        // Then
        assertThat(result).isEqualTo(states)
    }

    @Test
    fun `getStatesByProjectId should null when data source returns empty list or no matches project`() = runTest {
        // Given
        coEvery { stateDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getStatesByProjectId(validState.projectId)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    // region getTasksByStateId
    @Test
    fun `getTasksByStateId should return list of matches tasks when data source returns tasks`() = runTest {
        // Given
        coEvery { taskDataSource.getAll() } returns tasks
        // When
        val result = repository.getTasksByStateId(validState.id)
        // Then
        assertThat(result).isEqualTo(tasks)
    }

    @Test
    fun `getTasksByStateId should null when data source returns empty list or no matches tasks`() = runTest {
        // Given
        coEvery { taskDataSource.getAll() } returns emptyList()
        // When
        val result = repository.getTasksByStateId(validState.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    // region updateState
    @Test
    fun `updateState should return success when update succeeds`() = runTest {
        // Given
        coEvery { stateDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `updateState should return success with state id when update succeeds`() = runTest {
        // Given
        coEvery { stateDataSource.update(any(), any()) } returns true
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validState.id)
    }

    @Test
    fun `updateState should return failure when update fails`()= runTest {
        // Given
        coEvery { stateDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `updateState should return failure with InvalidStateException when update fails`()= runTest {
        // Given
        coEvery { stateDataSource.update(any(), any()) } returns false
        // When
        val result = repository.updateState(validState)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region deleteState
    @Test
    fun `deleteState should return success when delete succeeds`()= runTest  {
        // Given
        coEvery { stateDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `deleteState should return success with state id when delete succeeds`()= runTest {
        // Given
        coEvery { stateDataSource.delete(any()) } returns true
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result.getOrNull()).isEqualTo(validState.id)
    }

    @Test
    fun `deleteState should return failure when delete fails`()= runTest {
        // Given
        coEvery { stateDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `deleteState should return failure with InvalidStateException when delete fails`() = runTest{
        // Given
        coEvery { stateDataSource.delete(any()) } returns false
        // When
        val result = repository.deleteState(validState.id)
        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(InvalidStateException::class.java)
    }
    // endregion

    // region getStateByTaskId
    @Test
    fun `getStateByTaskId should return state when data sources returns state matches task`() = runTest{
        // Given
        coEvery { taskDataSource.getById(any()) } returns validTask
        coEvery { stateDataSource.getById(any()) } returns validState
        // When
        val result = repository.getStateByTaskId(validTask.id)
        // Then
        assertThat(result).isEqualTo(validState)
    }

    @Test
    fun `getStateByTaskId should return null when data sources returns null no match state and no task`() = runTest{
        // Given
        coEvery { taskDataSource.getById(any()) } returns null
        coEvery { stateDataSource.getById(any()) } returns null
        // When
        val result = repository.getStateByTaskId(validTask.id)
        // Then
        assertThat(result).isNull()
    }
    // endregion

    companion object {
        val validState = State(
            id = "st123", name = "ToDo", projectId = "pppppp"
        )
        val validTask = Task(
            id = "t6665",
            projectId = "hhhhh",
            title = "zzzz",
            description = null,
            stateId = "st123",
            assignedToUserId = "57r",
            createByUserId = "r444"
        )
        val states = listOf(
            State(id = "st123", name = "ToDo", projectId = "pppppp"),
            State(id = "st456", name = "InProgress", projectId = "pppppp"),
            State(id = "st789", name = "Done", projectId = "pppppp")
        )
        val tasks = listOf(
            Task(
                id = "t6665",
                projectId = "hhhhh",
                title = "zzzz",
                description = null,
                stateId = "st123",
                assignedToUserId = "57r",
                createByUserId = "r444"
            ), Task(
                id = "t4576",
                projectId = "p657",
                title = "Task1",
                description = null,
                stateId = "st123",
                assignedToUserId = "u66",
                createByUserId = "t4"
            ), Task(
                id = "t897",
                projectId = "p4566",
                title = "Task2",
                description = null,
                stateId = "st123",
                assignedToUserId = "y66",
                createByUserId = "l99"
            ), Task(
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