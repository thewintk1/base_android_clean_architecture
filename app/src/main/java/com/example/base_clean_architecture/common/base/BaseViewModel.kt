package com.example.base_clean_architecture.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base_clean_architecture.R
import com.example.base_clean_architecture.domain.errors.BackendTypedError
import kotlinx.coroutines.flow.*
import com.example.base_clean_architecture.domain.errors.DomainError
import com.example.base_clean_architecture.domain.errors.ViewError
import com.example.base_clean_architecture.utils.Constant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

abstract class BaseViewModel : ViewModel() {
    private val _progressStateFlow = MutableStateFlow(false)
    val progressFlow = _progressStateFlow.asStateFlow()

    private val _errorsFlow = MutableSharedFlow<DomainError>(0, 1)
    protected val jobExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.message?.let {
            val error: DomainError = DomainError.ApiError<BackendTypedError>(httpCode = -1, httpMessage = it)
            viewModelScope.launch {
                _errorsFlow.emit(error)
            }
        }
    }

    val errorsFlow: Flow<ViewError> = _errorsFlow.asSharedFlow().map { domainError ->
        when {
            domainError is DomainError.NetworkException -> {
                ViewError.ResourceError(
                    getResIdErrorResource(domainError.errorCode), domainError.errorCode
                )
            }

            domainError.errorMessage.isEmpty() -> {
                ViewError.ResourceError(R.string.error_something_went_wrong, domainError.errorCode)
            }

            else -> {
                ViewError.StringError(domainError.errorMessage, domainError.errorCode)
            }
        }
    }

    suspend fun emitError(error: DomainError) {
        _errorsFlow.emit(error)
    }

    fun emitLoadingState(isLoading: Boolean) {
        viewModelScope.launch {
            _progressStateFlow.value = isLoading
        }
    }

    private fun getResIdErrorResource(errorCode: String): Int {
        return when (errorCode) {
            Constant.ERROR_CODE_NETWORK_NO_NETWORK -> R.string.exception_no_network
            Constant.ERROR_CODE_NETWORK_DNS -> R.string.exception_unknownhost
            Constant.ERROR_CODE_NETWORK_CONNECTION_TIME_OUT -> R.string.exception_connect_timeout
            Constant.ERROR_CODE_NETWORK_SOCKET_TIME_OUT -> R.string.exception_socket_timeout
            Constant.ERROR_CODE_NETWORK_SOCKET -> R.string.exception_socket
            Constant.ERROR_CODE_NETWORK_SSL_HAND_SHAKE -> R.string.exception_ssl_handshake
            Constant.ERROR_CODE_NETWORK_SSL_PEER_UNVERIFIED -> R.string.exception_ssl_handshake
            else -> error(errorCode)
        }
    }
}
