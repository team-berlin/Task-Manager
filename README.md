# 🧭 PlanMate - Task Management CLI App

**PlanMate** is a command-line task management application built in **Kotlin**.  
It helps teams manage their projects, tasks, and workflows efficiently using a role-based access system and an audit trail.

The app was built using **Test-Driven Development (TDD)** and follows **SOLID** principles, ensuring high-quality, maintainable code.

---

## Key Features

### User Roles
- **Admin**
  - Can manage users, projects, and workflow states.
  - Can view audit logs.
- **Mate**
  - Can create, edit, delete, and view tasks within assigned projects.

### Authentication
- Users log in with a username and password.
- Passwords are stored securely using **MD5 hashing**.

### Projects & Tasks
- Projects can be created and customized by Admins.
- Tasks belong to projects and can be managed by both Admins and Mates.
- Task states (TODO, In Progress, Done) are fully customizable by Admins.

### Task Display (Swimlanes UI)
- Tasks are grouped and displayed by state in a clear swimlanes format in the terminal.

### Audit Logging
- Every change to a project or task is logged with:
  - The user who made the change.
  - What was changed.
  - When the change happened.
- Users can view logs filtered by **project ID** or **task ID**.

---

## Architecture

- **Layered Architecture**:
  - `UI Layer`: Command-line interface.
  - `Domain Layer`: Core business logic (clean and testable).
  - `Data Layer`: Persistence (originally CSV, now MongoDB).

- **Uni-directional Dependencies**:
  - Data → Domain
  - UI → Domain

> Domain layer is independent from data and UI for better testability and maintainability.

- **Dependency Injection**:
  - Uses **Koin** for clean dependency management.

---

## MongoDB Migration Update

We are migrating the data source from local CSV files to **MongoDB** for cloud-based shared access.
---

## Testing

- Developed using **Test-Driven Development (TDD)**.
- Maintains **80%+ test coverage** across:
  - Business rules
  - Data repositories
  - UI interactions

---
## 🎥 Demo Video

[Click here to watch the demo](https://drive.google.com/file/d/1lcma_BLuRiCR-yAugwBVV9GdSYL8YQoW/view?usp=drive_link) 
---
## ▶️ How to Run

> Make sure you have Kotlin and MongoDB installed.

```bash
# Compile the project
./gradlew build

# Run the CLI app
./gradlew run
