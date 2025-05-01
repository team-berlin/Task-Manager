package logic.hashPassword

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class MD5HashAlgorithmTest {
    private lateinit var mD5HashAlgorithm: HashingPassword

    @BeforeEach
    fun setup() {
        mD5HashAlgorithm = MD5Hasher()
    }

    @Test
    fun `hashPassword should return  482c811da5d5b4bc6d497ffa98491e38  when password equal password123`() {
        //Given
        val password = "password123"

        // when
        val result = mD5HashAlgorithm.hashPassword(password)

        // Then
        assertThat(result).isEqualTo("482c811da5d5b4bc6d497ffa98491e38")
    }

    @Test
    fun `hashPassword should return same hash when input same password `() {
        //Given
        val password = "password123"

        //when
        val resultInputOne = mD5HashAlgorithm.hashPassword(password)
        val resultInputTwo = mD5HashAlgorithm.hashPassword(password)

        //Then
       assertThat(resultInputOne).isEqualTo(resultInputTwo)
    }
}
