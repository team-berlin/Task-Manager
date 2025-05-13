package com.berlin.domain.helper

import com.berlin.domain.model.user.User

object AuthServiceTestData {
    val inValidUserName = "Ahmed"
    val inValidUserPassword = "00000000"
    val userName = "Fatma"
    val testUserPassword = "123456"
    val hashPassword = "hashed_123456"
    val fakeId = "generated-id-456"
    val userNameIsEmpty = ""
    val userPassword = "hashed_securePassword"
    val userPasswordIsEmpty = ""
    val passwordLessthanEight = "548"
    val generatedId = "generated-id"
    val existingId = ""
    val adminIsFirstUser = userDummyData("u55", "Menna", User.UserRole.ADMIN)
    val existingUser = userDummyData("u13", "Menna", User.UserRole.ADMIN)
    val testForUserName = "Fatma"
    val testForUserPassword = "1234567899"
    val idExist = "u13"
    val idNotExist = "u6"
    val idWithSpacesExist=" u13"
    val expectedUser = userDummyData(
        userName = userName,
        role = User.UserRole.MATE
    )
    val user = userDummyData(
        userName = "Fatma",
        role = User.UserRole.ADMIN
    )

}

val EMPTY_USER =  User("","", User.UserRole.ADMIN)
val CACHEUSER =User("user1234", "admin", User.UserRole.ADMIN)