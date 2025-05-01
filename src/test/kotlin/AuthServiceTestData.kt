import com.berlin.domain.model.User
import com.berlin.domain.model.UserRole
import com.berlin.userDummyData
import domain.model.Permission

object AuthServiceTestData {

    val inValidUserName= "Ahmed"
    val inValidUserPassword = "00000000"
    val user = userDummyData(userName = "Fatma", password = "hashed_securePassword" , permission =
    Permission(
        createTask = true,
        editTask = true,
        deleteTask= true,
        viewAuditLogs= true
    ))
    val passwordLessThanEight = "hashd_s"
    val userName = "Fatma"
    val userNameIsEmpty =""
    val userPassword = "hashed_securePassword"
    val userPasswordIsEmpty = ""
    val adminIsFirstUser= userDummyData("55","Menna","12345678",  permission = Permission(viewAuditLogs = true))
    val existingUser = userDummyData("13", "Menna", "12345678",  permission = Permission(viewAuditLogs = true))
    val idNotExist = "6"
    val userNewPassword ="548746874897"
    val idExist = "13"
    val excepctedUser = userDummyData(
        userName = userName,
        password = userPassword,
        permission = Permission(viewAuditLogs = true),
        role = UserRole.MATE
    )
    val unKnown = "dsd"

}