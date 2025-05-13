package com.berlin.data.schema

import com.berlin.data.csv_data_source.schema.UserSchema
import com.berlin.data.dto.UserDto
import com.berlin.domain.model.user.User
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class UserSchemaTest {

    private lateinit var userSchema: UserSchema

    @BeforeEach
    fun setup() {
        userSchema = fakeUserSchema()
    }

    //region create object

    @Test
    fun `should throw IllegalArgumentException when try to create object with blank file name`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            userSchema = UserSchema("", listOf("a", "b", "c", "d"))
        }
    }

    @Test
    fun `should throw IllegalArgumentException when try to create object with invalid size header`() {
        //when //then
        assertThrows<IllegalArgumentException> {
            userSchema = UserSchema("test.csv", listOf("a", "b"))
        }
    }

    //endregion

    //region toRow

    @Test
    fun `toRow should return list of valid user attributes when valid user passed`() {
        //when
        val result = userSchema.toRow(validUser)
        //then
        assertThat(result).isEqualTo(validRowAdmin)
    }

    @Test
    fun `toRow should return list of valid user attributes when valid user mate passed`() {
        //when
        val result = userSchema.toRow(validUserMate)
        //then
        assertThat(result).isEqualTo(validRowMate)
    }

    @Test
    fun `toRow should return empty list when invalid user passed miss id attribute`() {
        //when
        val result = userSchema.toRow(invalidUserEmptyId)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid user passed miss userName attribute`() {
        //when
        val result = userSchema.toRow(invalidUserEmptyUserName)
        //then
        assertThat(result).isEmpty()
    }

    @Test
    fun `toRow should return empty list when invalid user passed miss password attribute`() {
        //when
        val result = userSchema.toRow(invalidUserEmptyPass)
        //then
        assertThat(result).isEmpty()
    }

    //endregion

    //region fromRow

    @Test
    fun `fromRow should return user when valid row admin passed`() {
        //when
        val result = userSchema.fromRow(validRowAdmin)
        //then
        assertThat(result).isEqualTo(validUser)
    }

    @Test
    fun `fromRow should return user when valid row mate passed`() {
        //when
        val result = userSchema.fromRow(validRowMate)
        //then
        assertThat(result).isEqualTo(validUserMate)
    }

    @Test
    fun `fromRow should return null when invalid row passed miss id column`() {
        //when
        val result = userSchema.fromRow(invalidRowEmptyId)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid user passed miss userName column`() {
        //when
        val result = userSchema.fromRow(invalidRowEmptyUserName)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid user passed miss password column`() {
        //when
        val result = userSchema.fromRow(invalidRowEmptyPass)
        //then
        assertThat(result).isNull()
    }

    @Test
    fun `fromRow should return null when invalid user passed miss role column`() {
        //when
        val result = userSchema.fromRow(invalidRowEmptyRole)
        //then
        assertThat(result).isNull()
    }

    //endregion

    //region getId

    @Test
    fun `getId should return id of user passed`() {
        //when
        val result = userSchema.getId(validUser)
        //then
        assertThat(result).isEqualTo(validUser.id)
    }

    @Test
    fun `getId should return null when user passed have empty id`() {
        //when
        val result = userSchema.getId(invalidUserEmptyId)
        //then
        assertThat(result).isNull()
    }

    //endregion

    private fun fakeUserSchema() = UserSchema("test.csv", listOf("a", "b", "c", "d"))

    private companion object {

        //region Some Users

        val validUserMate = UserDto(
            id = "abcs123", userName = "marwanMahmoud", password = "ui76654898", role = User.UserRole.MATE
        )
        val validUser = UserDto(
            id = "abcs123", userName = "marwanMahmoud", password = "ui76654898", role = User.UserRole.ADMIN
        )
        val invalidUserEmptyId = UserDto(
            id = "", userName = "marwanMahmoud", password = "ui76654898", role = User.UserRole.ADMIN
        )
        val invalidUserEmptyUserName = UserDto(
            id = "abcs123", userName = "", password = "ui76654898", role = User.UserRole.ADMIN
        )
        val invalidUserEmptyPass = UserDto(
            id = "abcs123", userName = "marwanMahmoud", password = "", role = User.UserRole.ADMIN
        )

        //endregion

        //region Some Rows

        val validRowAdmin = listOf(
            "abcs123", "marwanMahmoud", "ui76654898", User.UserRole.ADMIN.toString()
        )
        val validRowMate = listOf(
            "abcs123", "marwanMahmoud", "ui76654898", User.UserRole.MATE.toString()
        )
        val invalidRowEmptyId = listOf(
            "", "marwanMahmoud", "ui76654898", User.UserRole.ADMIN.toString()
        )
        val invalidRowEmptyUserName = listOf(
            "abcs123", "", "ui76654898", User.UserRole.ADMIN.toString()
        )
        val invalidRowEmptyPass = listOf(
            "abcs123", "marwanMahmoud", "", User.UserRole.ADMIN.toString()
        )
        val invalidRowEmptyRole = listOf(
            "abcs123", "marwanMahmoud", "ui76654898", ""
        )

        //endregion
    }
}