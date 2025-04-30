package com.berlin.domain.exception

class TaskAlreadyExistsException(message: String) : IllegalStateException(message)
class TaskNotFoundException(message: String) : NoSuchElementException(message)
class InputCancelledException(message: String) : RuntimeException(message)
class InvalidSelectionException(message: String) : RuntimeException(message)
