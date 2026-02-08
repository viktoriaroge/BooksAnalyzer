package com.viroge.booksanalyzer.data.remote

import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

object NetworkErrorMapper {

    fun map(t: Throwable): AppNetworkError = when (t) {
        is AppNetworkError -> t
        is HttpException -> AppNetworkError.Http(t.code(), safeBody(t))
        is SocketTimeoutException -> AppNetworkError.Timeout
        is IOException -> AppNetworkError.NoConnection
        else -> AppNetworkError.Unknown(t)
    }

    private fun safeBody(t: HttpException): String? =
        try {
            t.response()?.errorBody()?.string()
        } catch (_: Throwable) {
            null
        }
}