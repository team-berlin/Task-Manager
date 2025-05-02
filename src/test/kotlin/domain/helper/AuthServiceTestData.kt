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
    val idNotExist = "o6"
    val idExist = "13y"
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
    val adminIsFirstUser = userDummyData("55", "Menna", "12345678", permission = Permission(viewAuditLogs = true))
    val existingUser = userDummyData("y13", "Menna", "12345678", permission = Permission(viewAuditLogs = true))

    private val existingUserID="Men_11"
    private val nonExistingID="non_1"
    private val existingIDUser= userDummyData(
        "Men_123","Menna","1234", permission = Permission(), UserRole.ADMIN
    )
}