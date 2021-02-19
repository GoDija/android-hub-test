package com.dijanow.featurelib.base

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.mutate(mutation: ((T) -> T)) {
	val newValue = mutation(value)

	value = newValue
}

fun <T> List<T>.findAndReplace(find: ((T) -> Boolean), replacement: ((T) -> T), throwIfNotFound: Boolean = false): List<T> {

	val indexItem = indexOfFirst { find(it) }

	if (indexItem == -1) {
		if (throwIfNotFound) throw IllegalArgumentException("Item not found")
		else return this
	}

	val newList = toMutableList()
	newList[indexItem] = replacement(get(indexItem))

	return newList.toList()
}

