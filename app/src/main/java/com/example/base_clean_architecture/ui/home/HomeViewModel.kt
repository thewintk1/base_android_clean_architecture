package com.example.base_clean_architecture.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.base_clean_architecture.common.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeViewModel : BaseViewModel() {
    var data = MutableLiveData<List<Int>>()

    private lateinit var job: Job

    fun fetchData() {
        emitLoadingState(true)

        job = viewModelScope.launch(jobExceptionHandler) {
            supervisorScope {
                launch {
                    val result = async { requestWithIndex(1, false) }
                    data.postValue(intArrayOf(result.await()).toList())
                }
                launch {
                    val result2 = async { requestWithIndex(2, false) }
                    data.postValue(intArrayOf(data.value?.get(0) ?: 0, result2.await()).toList())
                }
            }
        }
        job.invokeOnCompletion {
            emitLoadingState(false)
            Timber.tag("###BaseProject").d("job finish")
        }
    }

    fun cancelJob() {
        job.cancel()
    }

    private suspend fun requestWithIndex(index: Int, isThrowException: Boolean = false): Int {

//        withContext(Dispatchers.IO) {
        Timber.tag("###BaseProject")
            .d("request with index $index at ${System.currentTimeMillis()}")
        val delayTime = index * 1000L
        delay(delayTime)
        if (isThrowException) {
            throw Exception("Exception with index $index")
            return 0
        }
        Timber.tag("###BaseProject").d("Done with index $index")
        return index
//        }
    }
}