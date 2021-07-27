package com.mithun.simplebible.data.repository

/**
 * wrapper class that encapsulates repository response to carry the network status
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Empty<T> : Resource<T>()
}
