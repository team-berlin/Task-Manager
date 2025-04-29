package com.berlin

import com.berlin.di.appModules
import com.berlin.model.User
import com.berlin.model.UserRole
import com.berlin.ui.TaskUI
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

fun main() {
    println("=== PlanMate v1.0 ===")
    println("A task management system")

    startKoin {
        modules(appModules)
    }

    val currentUser = User(
        id = "USER-001",
        userName = "admin",
        password = "5f4dcc3b5aa765d61d8327deb882cf99", // MD5 hash of "password"
        role = UserRole.ADMIN
    )

    mainMenu(currentUser)
}

fun mainMenu(currentUser: User) {
    val taskUI: TaskUI by inject(TaskUI::class.java)

    while (true) {
        println("\n===== Main Menu =====")
        println("1. Task Management")
        println("2. Project Management")
        println("3. User Management")
        println("4. Exit")
        print("Enter your choice: ")

        when (readLine()?.trim()) {
            "1" -> taskUI.displayTaskMenu(currentUser)
            "2" -> println("Project Management - Feature not implemented yet")
            "3" -> println("User Management - Feature not implemented yet")
            "4" -> {
                println("Exiting PlanMate. Goodbye!")
                return
            }
            else -> println("Invalid option. Please try again.")
        }
    }
}