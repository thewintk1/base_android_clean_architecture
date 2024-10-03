package com.example.base_clean_architecture.domain.errors

sealed class ViewError( val errorCode: String?) {
    class ResourceError(val resId: Int, private val code: String?) : ViewError(code)
    class StringError(val error: String, private val code: String?) : ViewError(code)
}