package com.berlin

object AuthServiceTestData {
    val userNameIsEmpty = userDummyData("1", "", password = "12356497")
    val allFieldsCorrect = userDummyData("2", "fatma", "12345678")
    val passwordLessThanEight = userDummyData("3", "fatma", "1234567")
    val userPasswordEmpty = userDummyData("4", "fatma", "")
    val adminIsFirstUser= userDummyData("55","Menna","12345678")
    val allFieldsAreEmpty = userDummyData("", "", "")
    val userIdIsRedundunt =  userDummyData("1", "", "")
    val userIdIsEmpty = userDummyData("", "fatma", "")
    val idNotExist = "6"
    val idExist = "13"
    val existingUser = userDummyData("13", "Menna", "12345678")
}