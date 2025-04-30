package logic.usecase

import com.berlin.AuthServiceTestData
import com.berlin.logic.usecase.AuthenticationService
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthenticationServiceTest {
    private lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setup() {
        authenticationService = AuthenticationService()
    }

    @Test
    fun `login should return user when user input valid data`() {
        //Given
        every { authenticationService.login("fatma", "5454897984") } returns AuthServiceTestData.allFieldsCorrect
        //When
        val result = authenticationService.login("fatma", "5454897984")
        //Then
        assertThat(result).isEqualTo(AuthServiceTestData.allFieldsCorrect)
    }

    @Test
    fun `login should return false when user is not logged in`() {
        //Given
        every { authenticationService.login("", "") } throws Exception()
        //When & Then
        assertThrows<Exception> {
            authenticationService.login("", "")
        }
    }

    //check
    @Test
    fun `login should return null when user input empty data`() {
        //Given
        every { authenticationService.login("", "") } returns null
        //When
        val result = authenticationService.login("", "")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `login should return null when user input invalid data`() {
        //When
        val result = authenticationService.login("fatma", "1546896464")
        //Then
        assertThat(result).isNull()

    }

    @Test
    fun `getAllUsers should return one user at least who is admin when there is no mate created yet`() {
        //Given
        every { authenticationService.getAllUsers() } returns listOf(AuthServiceTestData.adminIsFirstUser)
        //when
        val result = authenticationService.getAllUsers()
        // then
        assertThat(result).isEmpty()

    }

    @Test
    fun `createUser should return false when user id is empty`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.userIdIsEmpty) } returns false
        //when
        val result = authenticationService.createMate(AuthServiceTestData.userIdIsEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user id is duplicated`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.userIdIsRedundunt) } returns false
        //when
        val result = authenticationService.createMate(AuthServiceTestData.userIdIsRedundunt)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user name is empty`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.userNameIsEmpty) } returns false
        //when
        val result = authenticationService.createMate(AuthServiceTestData.userPasswordEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user password is empty`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.userPasswordEmpty) } returns false
        //when
        val result = authenticationService.createMate(AuthServiceTestData.userNameIsEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return IllegalArgumentException when password less then 8 numbers`() {
        //Given
        val dummy = AuthServiceTestData.passwordLessThanEight
        //when & Then
        assertThrows<IllegalArgumentException> {
            authenticationService.createMate(dummy)
        }
    }

    @Test
    fun `createUser should return IllegalArgumentException when all fields empty`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.allFieldsAreEmpty) } throws IllegalArgumentException(
            "fields shouldn't empty"
        )
        //When & Then
        assertThrows<IllegalArgumentException> {
            authenticationService.createMate(AuthServiceTestData.allFieldsAreEmpty)
        }
    }

    @Test
    fun `createUser should return true when all fields is correct`() {
        //Given
        every { authenticationService.createMate(AuthServiceTestData.allFieldsCorrect) } returns true
        //when
        val result = authenticationService.createMate(AuthServiceTestData.allFieldsCorrect)
        //Then
        assertThat(result).isTrue()

    }

    @Test
    fun `getUserById should return null when id isn't exist`() {
        //Given
        every { authenticationService.getUserById(AuthServiceTestData.idNotExist) } returns null
        //when
        val result = authenticationService.getUserById("6")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return null when id is empty`() {
        //Given
        every { authenticationService.getUserById(AuthServiceTestData.idEmpty) } returns null
        //when
        val result = authenticationService.getUserById("6")
        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return user of given id`() {
        //Given
        every { authenticationService.getUserById(AuthServiceTestData.idExist) } returns AuthServiceTestData.existingUser
        //when
        val result = authenticationService.getUserById("13")
        //Then
        assertThat(result).isEqualTo(AuthServiceTestData.existingUser)
    }

    @Test
    fun `getCurrentUser should return null when there is no one log in the system`() {
        //Given
        every { authenticationService.getCurrentUser() } returns null

        //when
        val result = authenticationService.getCurrentUser()

        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getCurrentUser should return users when they logged in the system`() {
        //Given
        every { authenticationService.getCurrentUser() } returns listOf(AuthServiceTestData.adminIsFirstUser)

        //when
        val result = authenticationService.getCurrentUser()

        //Then
        assertThat(result).isEqualTo(AuthServiceTestData.adminIsFirstUser)
    }

}