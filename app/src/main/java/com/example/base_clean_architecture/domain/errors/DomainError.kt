package com.example.base_clean_architecture.domain.errors

import com.google.gson.annotations.SerializedName
import java.io.IOException

const val ERROR_CODE_SYSTEM = "-100"
const val ERROR_CODE_NETWORK_DNS = "-101"
const val ERROR_CODE_NETWORK_CONNECTION_TIME_OUT = "-102"
const val ERROR_CODE_NETWORK_SOCKET_TIME_OUT = "-103"
const val ERROR_CODE_NETWORK_SOCKET = "-104"
const val ERROR_CODE_NETWORK_SSL_HAND_SHAKE = "-105"
const val ERROR_CODE_NETWORK_SSL_PEER_UNVERIFIED = "-106"
const val ERROR_CODE_NETWORK_NO_NETWORK = "-107"

interface DomainErrorInterface {
    val errorCode: String
    val errorMessage: String
}

data class StandardError(
    @SerializedName("message") val message: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("context") val context: Any? = null,
) : BackendTypedError

sealed class DomainError : DomainErrorInterface {
    data class ApiError<T : BackendTypedError>(
        val httpCode: Int,
        val httpMessage: String,
        val error: T? = null
    ) : DomainError() {

        override val errorCode
            get() = when (error) {
                is StandardError -> when {
                    error.code != null -> error.code
                    else -> httpCode.toString()
                }
                else -> httpCode.toString()
            }

        override val errorMessage
            get() = when (error) {
                is StandardError -> {
                    when (error.context) {
                        is Map<*, *> -> error.context.values.joinToString(separator = "\n")
                            .replace("[","")
                            .replace("]","")
                        else ->
                            error
                            .message ?: httpMessage

                    }
                }
                else -> httpMessage
            }
    }

    data class NetworkException(val throwable: Throwable) : DomainError() {
        override val errorCode: String
            get() = when (throwable) {
                is NoNetworkException -> ERROR_CODE_NETWORK_NO_NETWORK
                is java.net.UnknownHostException -> ERROR_CODE_NETWORK_DNS
                is java.net.ConnectException -> ERROR_CODE_NETWORK_CONNECTION_TIME_OUT
                is java.net.SocketTimeoutException -> ERROR_CODE_NETWORK_SOCKET_TIME_OUT
                is java.net.SocketException -> ERROR_CODE_NETWORK_SOCKET
                is javax.net.ssl.SSLHandshakeException -> ERROR_CODE_NETWORK_SSL_HAND_SHAKE
                is javax.net.ssl.SSLPeerUnverifiedException -> ERROR_CODE_NETWORK_SSL_PEER_UNVERIFIED
                else -> error("unexpected: $throwable")
            }

        override val errorMessage
            get() = throwable.javaClass.canonicalName ?: ""
    }

    data class SystemException(val throwable: Throwable) : DomainError() {
        override val errorCode: String
            get() = ERROR_CODE_SYSTEM
        override val errorMessage: String
            get() = throwable.message.toString()
    }
}

class NoNetworkException(message: String = "No internet connection") : IOException(message)

interface BackendTypedError
