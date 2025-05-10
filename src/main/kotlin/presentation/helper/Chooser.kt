package com.berlin.presentation.helper

import com.berlin.domain.exception.InputCancelledException
import com.berlin.domain.exception.InvalidSelectionException
import com.berlin.presentation.io.Reader
import com.berlin.presentation.io.Viewer

fun <T> choose(
    title: String, elements: List<T>, labelOf: (T) -> String, viewer: Viewer, reader: Reader
): T {
    if (elements.isEmpty()) throw InvalidSelectionException("No $title available.")

    viewer.show("--- $title ---")
    elements.forEachIndexed { i, e -> viewer.show("${i + 1}. ${labelOf(e)}") }
    viewer.show("X – Cancel\nSelect:")

    val input = reader.read()?.trim().orEmpty()
    if (input.equals("x", true)) throw InputCancelledException("")

    val idx = input.toIntOrNull()?.minus(1) ?: throw InvalidSelectionException("Not a number.")
    return elements.getOrNull(idx) ?: throw InvalidSelectionException("Out of range.")
}