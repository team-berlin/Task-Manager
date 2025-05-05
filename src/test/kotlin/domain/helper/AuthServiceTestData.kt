package com.berlin.domain.helper

import com.berlin.domain.model.UserRole
import com.berlin.logic.helper.userDummyData
import com.berlin.domain.model.User

object AuthServiceTestData {
    val inValidUserName = "Ahmed"
    val inValidUserPassword = "00000000"
    val passwordLessThanEight = "hashd_s"
    val userName = "Fatma"
    val userNameIsEmpty = ""
    val userPassword = "hashed_securePassword"
    val userPasswordIsEmpty = ""
    val idNotExist = "u6"
    val idExist = "u13"
    val excepctedUser = userDummyData(
        userName = userName,
        password = userPassword,
        role = UserRole.MATE
    )
    val user = userDummyData(
        userName = "Fatma", password = "hashed_securePassword"
    )
    val userIsEmpty = userDummyData("","","")
    val testForUserName = "Fatma"
    val testForUserPassword = "1234567899"
    val adminIsFirstUser = userDummyData("u55", "Menna", "12345678")
    val existingUser = userDummyData("u13", "Menna", "12345678")

    const val testUserName = "testUser"
    const val testUserPassword = "testPassword"
    const val invalidInput = ""

    val expectedUser = User(
        id = "id_testPassword",
        userName = testForUserName,
        password = "hashed_testPassword",
        role = UserRole.MATE
    )

    val emptyUser = User(
        id = "id_",
        userName = "",
        password = "hashed_",
        role = UserRole.MATE
    )


}


