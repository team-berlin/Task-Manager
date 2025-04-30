package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.logic.usecase.GetUserByIDUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetUserByIDUseCaseTest {
    private lateinit var repository: AuthenticationRepository
    private lateinit var getUserByIDUseCase: GetUserByIDUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getUserByIDUseCase = GetUserByIDUseCase(repository)
    }

    @Test
    fun `getUserById should return null when id isn't exist`() {
        //Given
        every { repository.getUserById(AuthServiceTestData.idNotExist) } returns null
        //when
        val result = getUserByIDUseCase.getUserById("6")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return null when id is empty`() {
        //when & Then
        assertThrows<IllegalArgumentException> {
            getUserByIDUseCase.getUserById("")
        }
    }

    @Test
    fun `getUserById should return user of given id`() {
        //Given
        every { repository.getUserById(AuthServiceTestData.idExist) } returns AuthServiceTestData.existingUser
        //when
        val result = getUserByIDUseCase.getUserById("13")
        //Then
        assertThat(result).isEqualTo(AuthServiceTestData.existingUser)
    }
}