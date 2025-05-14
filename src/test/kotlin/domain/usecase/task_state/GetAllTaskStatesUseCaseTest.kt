package com.berlin.domain.usecase.task_state

import com.berlin.data.DummyData
import com.berlin.domain.repository.TaskStateRepository
import io.mockk.every
import io.mockk.mockk
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class GetAllTaskStatesUseCaseTest {

    private lateinit var stateRepository: TaskStateRepository
    private lateinit var getAllStatesUseCase: GetAllTaskStatesUseCase

    @Before
    fun setUp() {
        stateRepository = mockk()
        getAllStatesUseCase = GetAllTaskStatesUseCase(stateRepository)
    }

    @Test
    fun `getAllStates should return states when there are stored states`() {

        every { stateRepository.getAllStates() } returns DummyData.states

        val result = getAllStatesUseCase()

        assertThat(result).isEqualTo(DummyData.states)
    }
}