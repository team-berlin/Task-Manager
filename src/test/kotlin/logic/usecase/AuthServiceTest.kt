package logic.usecase

import com.berlin.logic.usecase.AuthService
import com.berlin.model.UserRole
import com.berlin.userDummyData
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.google.common.truth.Truth.assertThat

class AuthServiceTest {
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        authService = AuthService()
    }

    @Test
    fun `getAllUsers should return one user who is admin when there is no user created yet`() {
        //Given
        every { authService.getAllUsers() } returns listOf(dummyAdminIsFirstUser)
        //when
        val result = authService.getAllUsers()
        // then
        assertThat(result).isEqualTo(dummyAdminIsFirstUser)

    }

    @Test
    fun `createUser should return false when user name is empty`() {
        //Given
        every { authService.createUser(dummyUserNameEmpty) } returns false
        //when
        val result = authService.createUser(dummyUserNameEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return false when user password is empty`() {
        //Given
        every { authService.createUser(dummyUserPasswordEmpty) } returns false
        //when
        val result = authService.createUser(dummyUserPasswordEmpty)
        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `createUser should return IllegalArgumentException when password less then 8 numbers`() {
        //Given
        val dummy = dummyPasswordLessThan8
        //when & Then
        assertThrows<IllegalArgumentException> {
            authService.createUser(dummy)
        }
    }

    @Test
    fun `createUser should return IllegalArgumentException when all fields empty`() {
        //Given
        every { authService.createUser(dummyAllFieldsEmpty) } throws IllegalArgumentException("fields shouldn't empty")
        //When & Then
        assertThrows<IllegalArgumentException> {
            authService.createUser(dummyAllFieldsEmpty)
        }
    }

    @Test
    fun `createUser should return true when all fields is correct`() {
        //Given
        every { authService.createUser(dummyAllFieldsCorrect) } returns true

        //when
        val result = authService.createUser(dummyAllFieldsCorrect)

        //Then
        assertThat(result).isTrue()

    }

    @Test
    fun `getUserById should return null when id isn't exist`() {
        //Given
        every { authService.getUserById(idNotExist) } returns null

        //when
        val result = authService.getUserById("6")

        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return null when id is empty`() {

        //Given
        every { authService.getUserById(idEmpty) } returns null
        //when
        val result = authService.getUserById("6")

        //Then
        assertThat(result).isNull()
    }

    @Test
    fun `getUserById should return user of given id`() {

        //Given
        every { authService.getUserById(idExist) } returns existingUser
        //when
        val result = authService.getUserById("13")

        //Then
        assertThat(result).isEqualTo(existingUser)
    }


    private companion object {
        val dummyUserNameEmpty = userDummyData("1", "", password = "12356497")
        val dummyAllFieldsCorrect = userDummyData("1", "Ahmed", "12345678")
        val dummyAdminIsFirstUser = userDummyData("0", "Ahmed", "12345678")
        val dummyPasswordLessThan8 = userDummyData("1", "Ahmed", "1234567")
        val dummyUserPasswordEmpty = userDummyData("1", "Ahmed", "")
        val dummyAllFieldsEmpty = userDummyData("", "", "")
        val idEmpty = ""
        val idNotExist = "6"
        val idExist = "13"
        val existingUser = userDummyData("13", "Menna", "1234", UserRole.ADMIN)
    }
}