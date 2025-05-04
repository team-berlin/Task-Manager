package com.berlin.domain.exception

class TaskAlreadyExistsException(message: String) : IllegalStateException(message)
class TaskNotFoundException(message: String) : NoSuchElementException(message)
class InputCancelledException(message: String) : RuntimeException(message)
class InvalidSelectionException(message: String) : RuntimeException(message)
class InvalidTaskTitle(message: String): Exception(message)
class InvalidAssigneeException(message: String): Exception(message)
class InvalidTaskStateException(message: String): Exception(message)
class InvalidProjectIdException(message: String): Exception(message)
class InvalidTaskIdException(message: String): Exception(message)
class InvalidCredentialsException(message: String): Exception(message)
class UserNotFoundException(message: String) : NoSuchElementException(message)
class InvalidUserIdException(message: String): Exception(message)
class UserNotLoggedInException(message: String) : NoSuchElementException(message)

class InvalidTaskException(message: String): Exception(message)