package com.berlin.domain.helper

import com.berlin.domain.model.UserRole
import com.berlin.logic.helper.userDummyData
import com.berlin.model.Permission

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
        permission = Permission(viewAuditLogs = true),
        role = UserRole.MATE
    )
    val user = userDummyData(
        userName = "Fatma", password = "hashed_securePassword", permission =
            Permission(
                createTask = true,
                editTask = true,
                deleteTask = true,
                viewAuditLogs = true
            )
    )
    val loginViews = listOf(
        "Enter your user name: ",
        "Enter your password: ",
        "Welcome fatma",
        "try again"

    )
    val createMateViews = listOf(
        "Enter user name: ",
        "Enter user password",
        "New mate is successfully created!",
        "something wrong please try again!"
    )
    val testUserName = "Fatma"
    val testUserPassword = "1234567899"
    val adminIsFirstUser = userDummyData("55", "Menna", "12345678", permission = Permission(viewAuditLogs = true))
    val existingUser = userDummyData("13", "Menna", "12345678", permission = Permission(viewAuditLogs = true))


}