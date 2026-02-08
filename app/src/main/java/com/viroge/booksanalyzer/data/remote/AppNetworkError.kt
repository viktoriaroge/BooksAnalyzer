package com.viroge.booksanalyzer.data.remote

sealed class AppNetworkError(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {

    data class Http(val code: Int, val body: String? = null) : AppNetworkError()
    data class Parse(val raw: String? = null) : AppNetworkError()
    data class Unknown(val t: Throwable) : AppNetworkError(cause = t)
    object NoConnection : AppNetworkError()
    object Timeout : AppNetworkError()
}
