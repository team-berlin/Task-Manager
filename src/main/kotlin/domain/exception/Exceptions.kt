package com.berlin.domain.exception

open class InvalidInputException(message: String) : Exception(message)
class InvalidTaskTitle(message: String) : InvalidInputException(message)
class InvalidAssigneeException(message: String) : InvalidInputException(message)
class InvalidTaskStateException(message: String) : InvalidInputException(message)
class InvalidProjectIdException(message: String) : InvalidInputException(message)
class InvalidTaskIdException(message: String) : InvalidInputException(message)
class InvalidCredentialsException(message: String) : InvalidInputException(message)
class InvalidUserIdException(message: String) : InvalidInputException(message)
class InvalidTaskException(message: String) : InvalidInputException(message)
class InvalidProjectException(message: String) : InvalidInputException(message)
class InvalidStateException(message: String) : InvalidInputException(message)
class InvalidAuditLogException(message: String) : InvalidInputException(message)
class InvalidStateIdException(message: String) : InvalidInputException(message)
class InvalidStateNameException(message: String) : InvalidInputException(message)

open class NotFoundException(message: String) : NoSuchElementException(message)
class TaskNotFoundException(message: String) : NotFoundException(message)
class StateNotFoundException(message: String) : NotFoundException(message)
class ProjectNotFoundException(message: String) : NotFoundException(message)
class UserNotFoundException(message: String) : NotFoundException(message)

open class SelectionException(message: String) : RuntimeException(message)
class InputCancelledException(message: String) : SelectionException(message)
class InvalidSelectionException(message: String) : SelectionException(message)

open class AlreadyExistsException(message: String) : IllegalStateException(message)
class TaskAlreadyExistsException(message: String) : AlreadyExistsException(message)
