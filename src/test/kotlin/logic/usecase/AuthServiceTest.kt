package logic.usecase

import com.berlin.TestData
import com.berlin.logic.usecase.AuthService
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthServiceTest {
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        authService = AuthService()
    }

    @Test
    fun `login should return user when user input valid data`() {
        //Given
        every { authService.login("fatma", "5454897984") } returns TestData.allFieldsCorrect
        //When
        val result = authService.login("fatma", "5454897984")
        //Then
        assertThat(result).isEqualTo(TestData.allFieldsCorrect)
    }

    @Test
    fun `login should return false when user is not logged in`() {
        //Given
        every { authService.login("", "") } throws Exception()
        //When & Then
        assertThrows<Exception> {
            authService.login("", "")
        }
    }

    //check
    @Test
    fun `login should return null when user input empty data`() {
        //Given
        every { authService.login("", "") } returns null
        //When
        val result = authService.login("", "")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `login should return null when user input invalid data`() {
        //When
        val result = authService.login("fatma", "1546896464")
        //Then
        assertThat(result).isNull()

    }

    @Test
    fun `getAllUsers should return empty list when there is no user created yet`() {
        //Given
        every { authService.getAllUsers() } returns emptyList()
        //when
        val result = authService.getAllUsers()
        // then
        assertThat(result).isEmpty()

    }

    @Test
    fun `createUser should return false when user id is empty`() {
        //Given
        every { authService.createMate(TestData.userIdIsEmpty) } returns false
        //when
        val result = authService.createMate(TestData.userIdIsEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user id is duplicated`() {
        //Given
        every { authService.createMate(TestData.userIdIsRedundunt) } returns false
        //when
        val result = authService.createMate(TestData.userIdIsRedundunt)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user name is empty`() {
        //Given
        every { authService.createMate(TestData.userNameIsEmpty) } returns false
        //when
        val result = authService.createMate(TestData.userPasswordEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user password is empty`() {
        //Given
        every { authService.createMate(TestData.userPasswordEmpty) } returns false
        //when
        val result = authService.createMate(TestData.userNameIsEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return IllegalArgumentException when password less then 8 numbers`() {
        //Given
        val dummy = TestData.passwordLessThanEight
        //when & Then
        assertThrows<IllegalArgumentException> {
            authService.createMate(dummy)
        }
    }

    @Test
    fun `createUser should return IllegalArgumentException when all fields empty`() {
        //Given
        every { authService.createMate(TestData.allFieldsAreEmpty) } throws IllegalArgumentException(
            "fields shouldn't empty"
        )
        //When & Then
        assertThrows<IllegalArgumentException> {
            authService.createMate(TestData.allFieldsAreEmpty)
        }
    }

    @Test
    fun `createUser should return true when all fields is correct`() {
        //Given
        every { authService.createMate(TestData.allFieldsCorrect) } returns true
        //when
        val result = authService.createMate(TestData.allFieldsCorrect)
        //Then
        assertThat(result).isTrue()

    }

    @Test
    fun `getUserById should return null when id isn't exist`() {
        //Given
        every { authService.getUserById(TestData.idNotExist) } returns null
        //when
        val result = authService.getUserById("6")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return null when id is empty`() {
        //Given
        every { authService.getUserById(TestData.idEmpty) } returns null
        //when
        val result = authService.getUserById("6")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return user of given id`() {
        //Given
        every { authService.getUserById(TestData.idExist) } returns TestData.existingUser
        //when
        val result = authService.getUserById("13")
        //Then
        assertThat(result).isEqualTo(TestData.existingUser)
    }

}