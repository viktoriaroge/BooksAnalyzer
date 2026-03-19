package com.viroge.booksanalyzer.data.remote

import retrofit2.HttpException

object NetworkErrorMapper {

    fun map(t: Throwable, source: ApiSource): AppNetworkError = when (t) {
        is AppNetworkError -> t

        is HttpException -> AppNetworkError.Http(t.code(), safeBody(t), source)

        is java.net.UnknownHostException -> AppNetworkError.NoConnection()

        is java.net.SocketTimeoutException -> AppNetworkError.Timeout(source)

        is javax.net.ssl.SSLException -> AppNetworkError.Security(source)

        is kotlinx.coroutines.CancellationException -> AppNetworkError.Cancelled(source)

        is java.io.IOException -> AppNetworkError.NoConnection()

        else -> AppNetworkError.Unknown(t, source)
    }

    private fun safeBody(t: HttpException): String? =
        try {
            t.response()?.errorBody()?.string()
        } catch (_: Throwable) {
            null
        }
}
