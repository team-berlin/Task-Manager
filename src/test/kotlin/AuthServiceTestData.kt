package com.berlin

object AuthServiceTestData {
    val inValidUserName= "Ahmed"
    val inValidUserPassword = "00000000"
    val user = userDummyData(userName = "Fatma", password = "54647996469879")
    val passwordLessThanEight = "1236547"
    val userName = "Fatma"
    val userNameIsEmpty =""
    val userPassword = "54647996469879"
    val userPasswordIsEmpty = ""
    val adminIsFirstUser= userDummyData("55","Menna","12345678")
    val existingUser = userDummyData("13", "Menna", "12345678")
    val idNotExist = "6"
    val idExist = "13"
}