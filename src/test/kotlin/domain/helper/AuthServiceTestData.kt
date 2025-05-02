package com.berlin.domain.helper

import com.berlin.domain.model.UserRole
import com.berlin.logic.helper.userDummyData

object AuthServiceTestData {
    val inValidUserName = "Ahmed"
    val inValidUserPassword = "00000000"
    val passwordLessThanEight = "hashd_s"
    val userName = "Fatma"
    val userNameIsEmpty = ""
    val userPassword = "hashed_securePassword"
    val userPasswordIsEmpty = ""
    val idNotExist = "6"
    val idExist = "13"
    val excepctedUser = userDummyData(
        userName = userName,
        password = userPassword,
        role = UserRole.MATE
    )
    val user = userDummyData(
        userName = "Fatma", password = "hashed_securePassword"
    )

    val testUserName = "Fatma"
    val testUserPassword = "1234567899"
    val adminIsFirstUser = userDummyData("55", "Menna", "12345678")
    val existingUser = userDummyData("13", "Menna", "12345678")


}


