package com.viroge.booksanalyzer.data.remote

sealed class AppNetworkError(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    class Http(
        val code: Int,
        val body: String?,
        val source: ApiSource,
    ) : AppNetworkError()

    class Timeout(
        val source: ApiSource,
    ) : AppNetworkError()

    class NoConnection : AppNetworkError()
    class Security(
        val source: ApiSource,
    ) : AppNetworkError()

    class Cancelled(
        val source: ApiSource,
    ) : AppNetworkError()

    class Unknown(
        val origin: Throwable,
        val source: ApiSource,
    ) : AppNetworkError()
}

enum class ApiSource {
    N_A, GOOGLE_BOOKS, OPEN_LIBRARY
}
