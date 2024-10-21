package com.example.base_clean_architecture.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base_clean_architecture.common.base.BaseViewModel
import com.example.base_clean_architecture.domain.errors.BackendTypedError
import com.example.base_clean_architecture.domain.errors.DomainError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeViewModel : BaseViewModel() {
    private var _liveData = MutableLiveData<List<Int>>()
    val liveData: LiveData<List<Int>> = _liveData

    private var _shareFlow = MutableSharedFlow<String>()
    val shareFlow: SharedFlow<String> = _shareFlow

    private fun fetchUsers(page: Int): Flow<List<String>> = flow {
        // Giả lập thời gian chờ (ví dụ như đang tải dữ liệu từ API)
        delay(2000)

        // Giả lập danh sách người dùng từ API
        val users = List(20) { "User ${it + (page - 1) * 20 + 1}" }

        // Phát ra danh sách người dùng
        emit(users)
    }

    // StateFlow để chứa danh sách người dùng
    private val _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users

    // Biến để lưu trữ trang hiện tại
    private var currentPage = 1

    // Tải danh sách người dùng với pagination
    fun loadUsers() {
        viewModelScope.launch {
            fetchUsers(currentPage).collect { newUsers ->
                // Thêm người dùng mới vào danh sách hiện tại
                _users.value += newUsers
            }
        }
    }

    // Làm mới danh sách người dùng (reset lại)
    fun refreshUsers() {
        viewModelScope.launch {
            // Đặt lại trang về 1
            currentPage = 1
            // Lấy danh sách mới và thay thế danh sách hiện tại
            fetchUsers(currentPage).collect { newUsers ->
                _users.value = newUsers
            }
        }
    }

    // Tăng trang và tải thêm người dùng
    fun loadMoreUsers() {
        currentPage += 1
        loadUsers()
    }

    private lateinit var job: Job

    fun fetchData() {
        emitLoadingState(true)

        job = viewModelScope.launch(jobExceptionHandler) {
            supervisorScope {
                val value = mutableListOf<Int>()
                launch {
                    val result = async { requestWithIndex(3, false) }
                    value.add(result.await())
                    _liveData.postValue(value)
                }
                launch {
                    val result2 = async { requestWithIndex(2, false) }
                    value.add(result2.await())
                    _liveData.postValue(value)
                }
            }
        }
        job.invokeOnCompletion {
            emitLoadingState(false)
            Timber.tag("###BaseProject").d("${Thread.currentThread().name} job finish")
        }
    }

    fun cancelJob() {
        try {
            job.cancel()
        } catch (_: Exception) {
            val error: DomainError =
                DomainError.ApiError<BackendTypedError>(
                    httpCode = -1,
                    httpMessage = "Job isn't exist ${Thread.currentThread().name}"
                )
            emitError(error)
        }
    }

    private suspend fun requestWithIndex(index: Int, isThrowException: Boolean = false): Int =
        withContext(Dispatchers.IO) {
            Timber.tag("###BaseProject")
                .d("${Thread.currentThread().name} request with index $index at ${System.currentTimeMillis()}")
            val delayTime = index * 1000L
            delay(delayTime)
            if (isThrowException) {
                throw Exception("Exception with index $index")
            }
            Timber.tag("###BaseProject").d("Done with index $index")
            index
        }
}
