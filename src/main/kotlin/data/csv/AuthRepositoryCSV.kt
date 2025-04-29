package com.berlin.data.csv

import com.berlin.logic.repositories.AuthenticationRepository
import com.berlin.model.User
import com.berlin.model.UserRole
import java.io.File
import java.io.FileWriter

class AuthRepositoryCSV(
    private val filePath: String = "users.csv"
) : AuthenticationRepository {

    private val delimiter = ","
    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.createNewFile()
            FileWriter(file).use { writer ->
                writer.append("id,userName,password,role\n")
                // إضافة مستخدم افتراضي للاختبار
                writer.append("USER-001,admin,5f4dcc3b5aa765d61d8327deb882cf99,ADMIN\n")
                writer.append("USER-002,mate1,5f4dcc3b5aa765d61d8327deb882cf99,MATE\n")
            }
        }
    }

    override fun createUser(user: User): Boolean {
        return try {
            val existingUsers = getAllUsers()
            if (existingUsers.any { it.id == user.id }) {
                return false
            }

            FileWriter(file, true).use { writer ->
                writer.append("${user.id}${delimiter}${user.userName}${delimiter}${user.password}${delimiter}${user.role}\n")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getUserById(userId: String): User? {
        return getAllUsers().find { it.id == userId }
    }

    override fun getAllUsers(): List<User> {
        if (!file.exists()) return emptyList()

        return file.readLines()
            .drop(1) // تخطي العنوان
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                try {
                    val parts = line.split(delimiter)
                    if (parts.size < 4) return@mapNotNull null

                    User(
                        id = parts[0],
                        userName = parts[1],
                        password = parts[2],
                        role = UserRole.valueOf(parts[3])
                    )
                } catch (e: Exception) {
                    null
                }
            }
    }
}