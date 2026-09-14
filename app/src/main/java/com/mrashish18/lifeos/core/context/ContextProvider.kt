package com.mrashish18.lifeos.core.context

/**
 * Modular provider interface supplying a specific contextual signal to the Context Engine.
 */
interface ContextProvider<out T> {
    fun provide(): T
}
