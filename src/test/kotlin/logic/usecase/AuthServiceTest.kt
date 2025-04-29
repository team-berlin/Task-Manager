package logic.usecase

import com.berlin.logic.usecase.AuthService
import com.berlin.userDummyData
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.zip.DataFormatException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServiceTest {
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        authService = AuthService()
    }

    @Test
    fun `createUser should return false when user name is empty`() {
        val dummy = userDummyData("1", "", password = "12356497")
        every { authService.createUser(dummy) } returns false
        val result = authService.createUser(dummy)
        assertFalse { result }
    }

    @Test
    fun `createUser should return false when user password is empty`() {
        val dummy = userDummyData("1", "Ahmed", "")
        every { authService.createUser(dummy) } returns false
        val result = authService.createUser(dummy)
        assertFalse { result }
    }

    @Test
    fun `createUser should return IllegalArgumentException when password less then 8 numbers`() {
        val dummy = userDummyData("1", "Ahmed", "1234567")
        assertThrows<IllegalArgumentException> {
            authService.createUser(dummy)
        }
    }

    @Test
    fun `createUser should return IllegalArgumentException when all fields empty`() {
        val dummy = userDummyData("", "", "")
        every { authService.createUser(dummy) } throws IllegalArgumentException("fields shouldn't empty")
        assertThrows<IllegalArgumentException> {
            authService.createUser(dummy)
        }
    }
    @Test
    fun `createUser should return true when all fields is correct`(){
        val dummy = userDummyData("1","Ahmed","12345678")
        every { authService.createUser(dummy) } returns true
        val result = authService.createUser(dummy)
        assertTrue { result }
    }

}