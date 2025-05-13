package com.berlin.domain.usecase.state

import com.berlin.domain.exception.InvalidStateException
import com.berlin.domain.exception.InvalidStateIdException
import com.berlin.domain.repository.StateRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class DeleteTaskStateUseCaseTest {
    private lateinit var deleteTaskStateUseCase: DeleteTaskStateUseCase
    private val stateRepository: StateRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        deleteTaskStateUseCase = DeleteTaskStateUseCase(stateRepository)
    }

    @Test
    fun `should return success when state is deleted successfully`() {
        // Given
        every { stateRepository.deleteState(any()) } returns "Deleted Successfully"
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = deleteTaskStateUseCase("state_1")

        // Then
        assertThat(result).isEqualTo("Deleted Successfully")
    }

    @Test
    fun `should return failure when state deletion fails`() {
        // Given
        every { stateRepository.deleteState(any()) } returns "Deletion Failed"
        every { stateRepository.getStateById(any()) } returns mockk()

        // When
        val result = deleteTaskStateUseCase("state_2")

        // Then
        assertThat(result).isEqualTo("Deletion Failed")

    }

    @Test
    fun `should throw exception when state does not exist`() {
        // Given
        val stateId = "q2"
        every { stateRepository.deleteState(any()) } throws InvalidStateException(stateId)

        // When & Then
        assertThrows<InvalidStateException> {  deleteTaskStateUseCase(stateId)}

    }

    @Test
    fun `should throw InvalidStateException when state does not exist`() {
        val stateId = "q2"
        every { stateRepository.deleteState(stateId) } throws InvalidStateException(stateId)

        assertThrows<InvalidStateException> {
            deleteTaskStateUseCase(stateId)
        }
    }

    @Test
    fun `should throw InvalidStateIdException when id is empty`() {
        val stateId = ""

        assertThrows<InvalidStateIdException> {
            deleteTaskStateUseCase(stateId)
        }
    }

    @Test
    fun `should throw InvalidStateIdException when id is blank`() {
        val stateId = "  "
        assertThrows<InvalidStateIdException> {
            deleteTaskStateUseCase(stateId)
        }
    }

    @Test
    fun `should throw InvalidStateIdException when id is just digits`() {
        val stateId = "123"

        assertThrows<InvalidStateIdException> {
            deleteTaskStateUseCase(stateId)
        }
    }


}
