package com.berlin.domain.helper

import com.berlin.domain.model.UserRole
import com.berlin.domain.model.User

object AuthServiceTestData {
    val inValidUserName = "Ahmed"
    val inValidUserPassword = "00000000"
    val userName = "Fatma"
    val userNameIsEmpty = ""
    val userPassword = "hashed_securePassword"
    val userPasswordIsEmpty = ""
    val idNotExist = "u6"
    val idWithSpacesExist=" u13"
    val idExist = "u13"
    val expectedUser = userDummyData(
        userName = userName,
        password = userPassword,
        role = UserRole.MATE
    )
    val user = userDummyData(
        userName = "Fatma", password = "hashed_securePassword"
    )
    val testForUserName = "Fatma"
    val testForUserPassword = "1234567899"
    val adminIsFirstUser = userDummyData("u55", "Menna", "12345678")
    val existingUser = userDummyData("u13", "Menna", "12345678")

    val EMPTY_USER =  User("","","",UserRole.ADMIN)
    val CACHEUSER =User("user1234", "admin", "1212", UserRole.ADMIN)


}